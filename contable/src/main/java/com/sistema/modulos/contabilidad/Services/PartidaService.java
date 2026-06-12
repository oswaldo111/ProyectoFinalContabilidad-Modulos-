package com.sistema.modulos.contabilidad.Services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.sistema.modulos.contabilidad.DAO.PartidaDAO;
import com.sistema.modulos.contabilidad.Models.DetallePartida;
import com.sistema.modulos.contabilidad.Models.Partida;

public class PartidaService {

	private static final String ESTADO_BORRADOR = "BORRADOR";
	private static final String ESTADO_MAYORIZADA = "MAYORIZADA";

	private final PartidaDAO partidaDAO;

	public PartidaService() {
		this.partidaDAO = new PartidaDAO();
	}

	public void guardarPartida(Partida partida) {
		validarPartida(partida);
		prepararPartida(partida);
		partidaDAO.insertar(partida);
	}

	public List<Partida> obtenerPartidas() {
		List<Partida> partidas = partidaDAO.listar();
		return partidas != null ? partidas : new ArrayList<>();
	}

	public Partida obtenerPartidaPorId(int idPartida) {
		return partidaDAO.buscarPorId(idPartida);
	}

	public void actualizarPartida(Partida partida) {
		validarPartida(partida);

		if (partida.getIdPartida() <= 0) {
			throw new IllegalArgumentException("La partida debe tener un identificador válido para actualizarse");
		}

		prepararPartida(partida);
		partidaDAO.actualizar(partida);
	}

	public void eliminarPartida(int idPartida) {
		if (idPartida <= 0) {
			throw new IllegalArgumentException("El identificador de la partida debe ser válido");
		}

		partidaDAO.eliminar(idPartida);
	}

	public void marcarComoMayorizada(Partida partida) {
		validarPartida(partida);
		validarPartidaBalanceada(partida);

		partida.setEstado(ESTADO_MAYORIZADA);
		prepararPartida(partida);
		partidaDAO.actualizar(partida);
	}

	public BigDecimal calcularTotalDebe(Partida partida) {
		return calcularTotal(partida, true);
	}

	public BigDecimal calcularTotalHaber(Partida partida) {
		return calcularTotal(partida, false);
	}

	public BigDecimal calcularDiferencia(Partida partida) {
		return calcularTotalDebe(partida).subtract(calcularTotalHaber(partida)).setScale(2, RoundingMode.HALF_UP);
	}

	public boolean estaBalanceada(Partida partida) {
		return calcularDiferencia(partida).compareTo(BigDecimal.ZERO) == 0;
	}

	public void validarPartida(Partida partida) {
		if (partida == null) {
			throw new IllegalArgumentException("La partida no puede ser nula");
		}

		if (partida.getIdEmpresa() <= 0) {
			throw new IllegalArgumentException("La partida debe pertenecer a una empresa válida");
		}

		if (partida.getNumeroPartida() <= 0) {
			throw new IllegalArgumentException("El número de partida debe ser mayor a cero");
		}

		Date fecha = partida.getFecha();
		if (fecha == null) {
			throw new IllegalArgumentException("La fecha de la partida es obligatoria");
		}

		if (partida.getDescripcionGeneral() == null || partida.getDescripcionGeneral().trim().isEmpty()) {
			throw new IllegalArgumentException("La descripción general es obligatoria");
		}

		if (partida.getDetalles() == null || partida.getDetalles().isEmpty()) {
			throw new IllegalArgumentException("La partida debe contener al menos un detalle");
		}

		for (DetallePartida detalle : partida.getDetalles()) {
			validarDetalle(detalle);
		}
	}

	public void validarPartidaBalanceada(Partida partida) {
		if (!estaBalanceada(partida)) {
			throw new IllegalArgumentException(
					"La partida no está balanceada. Debe y haber deben ser iguales. Diferencia: "
							+ calcularDiferencia(partida));
		}
	}

	private void prepararPartida(Partida partida) {
		partida.setDescripcionGeneral(partida.getDescripcionGeneral().trim());

		String estado = partida.getEstado();
		if (estado == null || estado.trim().isEmpty()) {
			partida.setEstado(ESTADO_BORRADOR);
		} else {
			partida.setEstado(estado.trim().toUpperCase());
		}

		if (partida.getDetalles() == null) {
			partida.setDetalles(new ArrayList<>());
			return;
		}

		for (DetallePartida detalle : partida.getDetalles()) {
			if (detalle == null) {
				throw new IllegalArgumentException("La lista de detalles contiene un elemento nulo");
			}

			if (detalle.getDebe() == null) {
				detalle.setDebe(BigDecimal.ZERO);
			}

			if (detalle.getHaber() == null) {
				detalle.setHaber(BigDecimal.ZERO);
			}

			detalle.setDebe(detalle.getDebe().setScale(2, RoundingMode.HALF_UP));
			detalle.setHaber(detalle.getHaber().setScale(2, RoundingMode.HALF_UP));

			if (detalle.getIdPartida() != partida) {
				detalle.setIdPartida(partida);
			}
		}
	}

	private void validarDetalle(DetallePartida detalle) {
		if (detalle == null) {
			throw new IllegalArgumentException("La lista de detalles contiene un elemento nulo");
		}

		if (detalle.getIdCuenta() == null || detalle.getIdCuenta().getIdCuenta() <= 0) {
			throw new IllegalArgumentException("Cada detalle debe tener una cuenta contable válida");
		}

		BigDecimal debe = normalizarMonto(detalle.getDebe());
		BigDecimal haber = normalizarMonto(detalle.getHaber());

		if (debe.compareTo(BigDecimal.ZERO) < 0 || haber.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Los importes de debe y haber no pueden ser negativos");
		}

		boolean tieneDebe = debe.compareTo(BigDecimal.ZERO) > 0;
		boolean tieneHaber = haber.compareTo(BigDecimal.ZERO) > 0;

		if (tieneDebe == tieneHaber) {
			throw new IllegalArgumentException(
					"Cada detalle debe registrar solo un lado: debe o haber, pero no ambos ni ninguno");
		}
	}

	private BigDecimal calcularTotal(Partida partida, boolean esDebe) {
		if (partida == null || partida.getDetalles() == null) {
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}

		BigDecimal total = BigDecimal.ZERO;

		for (DetallePartida detalle : partida.getDetalles()) {
			if (detalle == null) {
				continue;
			}

			BigDecimal monto = esDebe ? detalle.getDebe() : detalle.getHaber();
			total = total.add(normalizarMonto(monto));
		}

		return total.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal normalizarMonto(BigDecimal monto) {
		return Objects.requireNonNullElse(monto, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
	}

	public void cerrarConexion() {
		PartidaDAO.cerrarFactory();
	}
}
