package com.sistema.modulos.ventas.Paneles;

import com.sistema.modulos.ventas.Daos.CategoriaDAO;
import com.sistema.modulos.ventas.Daos.ClienteDAO;
import com.sistema.modulos.ventas.Daos.ProductoDAO;
import com.sistema.modulos.ventas.Model.Categoria;
import com.sistema.modulos.ventas.Model.Cliente;
import com.sistema.modulos.ventas.Model.Productos;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.sistema.modulos.ventas.Paneles.JPCliente;
import java.awt.PopupMenu;
import com.sistema.modulos.ventas.Daos.VentaDAO;
import java.time.format.DateTimeParseException;
import com.sistema.modulos.ventas.Model.Venta;
import com.sistema.modulos.ventas.Model.DetalleVenta;
import java.util.ArrayList;

/**
 *
 * @author Imanol
 */
public class JPVentas extends javax.swing.JPanel {

    private List<Categoria> listaCategorias;
    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;
    private List<Cliente> listaClientes;
    private ClienteDAO clienteDAO;
    private List<Productos> listaProductos; // Lista global para guardar los productos filtrados
    private javax.swing.table.DefaultTableModel modeloDetalle; // <-- Variable global
    private VentaDAO ventaDAO = new VentaDAO();

    public JPVentas() {

        initComponents();
        // 1. PRIMERO: Se inicializan TODOS los DAOs (Los constructores en memoria)
        crearDetalle();
        this.categoriaDAO = new CategoriaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteDAO = new ClienteDAO();

        // 2. SEGUNDO: Hasta que todos los DAOs existan, mandas a llamar los métodos de carga
        cargarCategorias();
        cargarClientes();
        // ¡Ponemos la fecha!
        colocarFechaActual();

        cargarNombreEmpresa();

        cmbCliente.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClienteActionPerformed(evt);
            }
        });
    }

    private void limpiarVenta() {

        // Tabla detalle PRIMERO
        if (modeloDetalle != null) {
            modeloDetalle.setRowCount(0);
        }

        // Cliente
        if (cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }

        // Documento
        cmbTipoDocumento.removeAllItems();
        cmbTipoDocumento.addItem("<Seleccione>");
        txtNumeroDocumento.setText("");

        // Producto
        if (cmbCategoriaProducto.getItemCount() > 0) {
            cmbCategoriaProducto.setSelectedIndex(0);
        }

        cargarProductosPorCategoria();

        if (cmbProducto.getItemCount() > 0) {
            cmbProducto.setSelectedIndex(0);
        }

        actualizarStockDisponible();

        // Totales
        txtMontoGravado.setText("0.00");
        txtPrecioIVA.setText("0.00");
        txtMontoExtenso.setText("0.00");
        txtPrecio.setText("0.00");

        // Pago
        txtEfectivo.setText("");
        txtSaldoPendiente.setText("0.00");
        txtEstadoVenta.setText("");

        // Fechas
        txtFechaVencimiento.setText("");
        colocarFechaActual();

        // ID factura
        txtIdFactura.setText("");
    }

    //Metodo para el id de la Empresa si no hay login el 1 y si hay la selecionada
    private void cargarNombreEmpresa() {
        System.out.println(">>> Entró a cargarNombreEmpresa en JPVentas");

        try {
            String nombreEmpresa = ventaDAO.obtenerNombreEmpresaActual();

            System.out.println(">>> Nombre recibido desde ClienteDAO: " + nombreEmpresa);

            if (nombreEmpresa == null || nombreEmpresa.trim().isEmpty()) {
                nombreEmpresa = "Empresa no encontrada";
            }

            txtEmpresa.setText(nombreEmpresa);
            txtEmpresa.setEditable(false);

        } catch (Exception e) {
            txtEmpresa.setText("Empresa no encontrada");
            txtEmpresa.setEditable(false);
            System.out.println(">>> Error al cargar empresa actual en ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }
//Para llenar las tabla

    private void crearDetalle() {
        // 1. Inicializamos el modelo y definimos que solo la columna 5 (Cantidad) sea editable
        modeloDetalle = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Solo la columna Cantidad se puede modificar
            }
        };

        // 2. Creamos los encabezados desde código
        modeloDetalle.addColumn("Línea");
        modeloDetalle.addColumn("Tipo");
        modeloDetalle.addColumn("Id");
        modeloDetalle.addColumn("Nombre");
        modeloDetalle.addColumn("Precio");
        modeloDetalle.addColumn("Cantidad");
        modeloDetalle.addColumn("Subtotal");

        // 3. Le asignamos el modelo a tu tabla de ventas
        tblVentas.setModel(modeloDetalle);

        // 4. Ajustamos los anchos de las columnas de forma fija
        tblVentas.getColumnModel().getColumn(0).setPreferredWidth(40);   // Línea
        tblVentas.getColumnModel().getColumn(1).setPreferredWidth(50);   // Tipo
        tblVentas.getColumnModel().getColumn(2).setPreferredWidth(40);   // Id
        tblVentas.getColumnModel().getColumn(3).setPreferredWidth(250);  // Nombre
        tblVentas.getColumnModel().getColumn(4).setPreferredWidth(80);   // Precio
        tblVentas.getColumnModel().getColumn(5).setPreferredWidth(70);   // Cantidad
        tblVentas.getColumnModel().getColumn(6).setPreferredWidth(90);   // Subtotal

        tblVentas.getTableHeader().setResizingAllowed(false);
        tblVentas.getTableHeader().setReorderingAllowed(false);

        // 5. Escuchador (Listener) para cuando el usuario cambie la cantidad con el teclado
        modeloDetalle.addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) {
                    return;
                }

                int fila = e.getFirstRow();
                int columna = e.getColumn();

                if (columna == 5) { // Si cambiaron la cantidad
                    Object valorPrecioObj = modeloDetalle.getValueAt(fila, 4);
                    Object valorCantidadObj = modeloDetalle.getValueAt(fila, 5);

                    if (valorCantidadObj != null && valorPrecioObj != null) {
                        try {
                            double cantidad = Double.parseDouble(valorCantidadObj.toString());
                            double precio = Double.parseDouble(valorPrecioObj.toString());

                            // Control de números arriba de 1
                            if (cantidad <= 0) {
                                cantidad = 1;
                                modeloDetalle.removeTableModelListener(this);
                                modeloDetalle.setValueAt(1, fila, 5);
                                modeloDetalle.addTableModelListener(this);
                            }

                            double subTotal = precio * cantidad;

                            // Actualizamos el subtotal en la columna 6
                            modeloDetalle.removeTableModelListener(this);
                            modeloDetalle.setValueAt(subTotal, fila, 6);
                            modeloDetalle.addTableModelListener(this);

                            // Recalcular el total general del formulario
                            calcularTotal();

                            // ==========================================================
                            // PUNTO A: SI CAMBIAN LA CANTIDAD DE LA CELDA, ACTUALIZA EL STOCK
                            // ==========================================================
                            actualizarStockDisponible();
                            // ==========================================================

                        } catch (NumberFormatException ex) {
                            // Si meten letras, lo regresamos a 1 automáticamente
                            modeloDetalle.removeTableModelListener(this);
                            modeloDetalle.setValueAt(1, fila, 5);
                            modeloDetalle.addTableModelListener(this);
                        }
                    }
                }
            }
        });

        // 6. NUEVO: Agregamos el KeyListener directamente por código para capturar "Suprimir"
        tblVentas.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                // VK_DELETE representa físicamente la tecla Suprimir / Delete
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    int filaSeleccionada = tblVentas.getSelectedRow();

                    if (filaSeleccionada != -1) {
                        // Detenemos momentáneamente eventos por si acaso
                        modeloDetalle.removeRow(filaSeleccionada);

                        // Opcional: Esto reacomoda los números de la columna "Línea" (1, 2, 3...) 
                        // para que no queden saltados si borras una fila intermedia
                        for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
                            modeloDetalle.setValueAt(i + 1, i, 0);
                        }

                        // Recalculamos el gran total ya sin ese producto
                        calcularTotal();

                        // ==========================================================
                        // PUNTO B: SI ELIMINAN LA FILA, REGRESA EL STOCK DISPONIBLE
                        // ==========================================================
                        actualizarStockDisponible();
                        // ==========================================================
                    }
                }
            }
        });

        txtPrecio.setText("0.0");
    }

    //Calcular el total de productos en la tabla
    private void calcularTotal() {

        if (modeloDetalle == null) {
            return;
        }

        double sumaSubtotales = 0.0;

        // 1. Sumar subtotales de la tabla
        for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
            Object valorSubtotal = modeloDetalle.getValueAt(i, 6);

            if (valorSubtotal != null) {
                try {
                    sumaSubtotales += Double.parseDouble(valorSubtotal.toString());
                } catch (NumberFormatException e) {
                    System.out.println("Error en fila " + i + ": " + e.getMessage());
                }
            }
        }

        // 2. Leer tipo de documento
        String tipoDoc = "";

        if (cmbTipoDocumento.getSelectedItem() != null) {
            tipoDoc = cmbTipoDocumento.getSelectedItem().toString().trim().toLowerCase();
        }

        // 3. Si no hay documento válido, limpiar totales
        if (tipoDoc.isEmpty()
                || tipoDoc.equals("<seleccione>")
                || tipoDoc.equals("<selecione>")) {

            txtMontoExtenso.setText("0.00");
            txtMontoGravado.setText("0.00");
            txtPrecioIVA.setText("0.00");
            txtPrecio.setText("0.00");
            txtEstadoVenta.setText("");
            txtSaldoPendiente.setText("0.00");
            return;
        }

        // 4. Como precio_venta YA incluye IVA:
        // Total = sumaSubtotales
        // Gravado = Total / 1.13
        // IVA = Total - Gravado
        // Exento = 0.00
        double precioTotal = sumaSubtotales;
        double montoGravado = precioTotal / 1.13;
        double montoIva = precioTotal - montoGravado;
        double montoExento = 0.0;

        // 5. Mostrar montos
        txtMontoExtenso.setText(String.format(java.util.Locale.US, "%.2f", montoExento));
        txtMontoGravado.setText(String.format(java.util.Locale.US, "%.2f", montoGravado));
        txtPrecioIVA.setText(String.format(java.util.Locale.US, "%.2f", montoIva));
        txtPrecio.setText(String.format(java.util.Locale.US, "%.2f", precioTotal));

        // 6. Leer dinero recibido
        double dineroRecibido = 0.0;
        String campoEfectivoText = txtEfectivo.getText().trim();

        if (!campoEfectivoText.isEmpty()) {
            try {
                dineroRecibido = Double.parseDouble(campoEfectivoText);
            } catch (NumberFormatException e) {
                dineroRecibido = 0.0;
            }
        }

        // 7. Calcular saldo y estado
        double saldoPendiente = precioTotal - dineroRecibido;
        String estadoVenta;

        if (saldoPendiente <= 0) {
            estadoVenta = "PAGADO";
            saldoPendiente = 0.0;
        } else if (dineroRecibido == 0) {
            estadoVenta = "PENDIENTE";
        } else {
            estadoVenta = "PARCIAL";
        }

        txtEstadoVenta.setText(estadoVenta);
        txtSaldoPendiente.setText(String.format(java.util.Locale.US, "%.2f", saldoPendiente));
    }

    //metodo para ver cuantos producto hay disponibles
    private void actualizarStockDisponible() {
        // PROTECCIÓN ULTRA: Si los componentes críticos aún no existen en memoria, salimos en silencio
        if (cmbProducto == null || txtExistencias == null || listaProductos == null) {
            return;
        }

        // 1. Validamos que haya un producto seleccionado en el combo
        String productoSeleccionado = (String) cmbProducto.getSelectedItem();
        if (productoSeleccionado == null) {
            txtExistencias.setText("0");
            return;
        }

        // 2. Buscamos el producto en la lista para saber su stock original
        Productos prodEncontrado = null;
        for (Productos prod : listaProductos) {
            if (prod.getNombreProducto().equals(productoSeleccionado)) {
                prodEncontrado = prod;
                break;
            }
        }

        if (prodEncontrado != null) {
            int idBuscado = prodEncontrado.getIdProducto();
            int stockOriginal = prodEncontrado.getExistencias();
            int cantidadEnTabla = 0;

            // 3. Escaneamos la tabla (Asegurando que el modelo de la tabla ya exista)
            if (modeloDetalle != null) {
                for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
                    int idEnTabla = Integer.parseInt(modeloDetalle.getValueAt(i, 2).toString());
                    if (idEnTabla == idBuscado) {
                        cantidadEnTabla = Integer.parseInt(modeloDetalle.getValueAt(i, 5).toString());
                        break;
                    }
                }
            }

            // 4. Hacemos la resta matemática en tiempo real
            int stockDisponible = stockOriginal - cantidadEnTabla;

            // 5. Pintamos el resultado
            txtExistencias.setText(String.valueOf(stockDisponible));

        } else {
            txtExistencias.setText("0");
        }
    }

    //Metodod para la fecha 
    private void colocarFechaActual() {
        // 1. Obtenemos la fecha actual del sistema/equipo
        LocalDate fechaActual = LocalDate.now();

        // 2. Definimos el formato que queremos (Día/Mes/Año)
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 3. Convertimos la fecha a texto con ese formato y la ponemos en el txt
        txtFechaEmision.setText(fechaActual.format(formato));
    }

    // 1. Método para las categorías (Mantiene tu misma lógica con escudos de seguridad)
    private void cargarCategorias() {
        // Si el combo o el DAO aún no se han creado en el arranque, evitamos el crash
        if (cmbCategoriaProducto == null || categoriaDAO == null) {
            return;
        }

        cmbCategoriaProducto.removeAllItems();
        listaCategorias = categoriaDAO.listarCategorias(); // Tu lista global

        if (listaCategorias != null) {
            for (com.sistema.modulos.ventas.Model.Categoria categoria : listaCategorias) {
                cmbCategoriaProducto.addItem(categoria.getCategoria()); // Solo Strings
            }
        }
    }

// 2. Método para cargar los productos (Mantiene tu misma lógica con escudos de seguridad)
    private void cargarProductosPorCategoria() {
        if (cmbProducto == null || cmbCategoriaProducto == null || productoDAO == null) {
            return;
        }

        cmbProducto.removeAllItems();
        String categoriaSeleccionada = (String) cmbCategoriaProducto.getSelectedItem();

        if (categoriaSeleccionada != null && listaCategorias != null) {
            int idCategoriaEncontrado = -1;
            for (com.sistema.modulos.ventas.Model.Categoria cat : listaCategorias) {
                if (cat.getCategoria().equals(categoriaSeleccionada)) {
                    idCategoriaEncontrado = cat.getIdCategoria();
                    break;
                }
            }

            if (idCategoriaEncontrado != -1) {
                // Asignamos el resultado a la variable global listaProductos
                listaProductos = productoDAO.listarProductosPorCategoria(idCategoriaEncontrado);

                if (listaProductos != null) {
                    for (com.sistema.modulos.ventas.Model.Productos prod : listaProductos) {
                        cmbProducto.addItem(prod.getNombreProducto());
                    }
                }
            }
        }
    }

    //Metodo para cargar los clientes en el combo 
    private void cargarClientes() {

        // Limpiamos el combo
        cmbCliente.removeAllItems();

        // Opción por defecto
        cmbCliente.addItem("<Seleccione Cliente>");

        // Traemos los clientes
        listaClientes = clienteDAO.listarClientesPorEmpresa();

        // Validación de seguridad
        if (listaClientes != null) {

            for (Cliente cliente : listaClientes) {

                cmbCliente.addItem(cliente.getNombre());

            }
        }

        // Dejamos seleccionada la opción por defecto
        cmbCliente.setSelectedIndex(0);
    }

    private void actualizarTipoDocumentoPorCliente() {

        cmbTipoDocumento.removeAllItems();
        cmbTipoDocumento.addItem("<Seleccione>");

        if (cmbCliente.getSelectedIndex() <= 0 || listaClientes == null) {
            return;
        }

        Cliente clienteSeleccionado = listaClientes.get(cmbCliente.getSelectedIndex() - 1);

        String nit = clienteSeleccionado.getNit();
        String nrc = clienteSeleccionado.getNrc();

        boolean tieneNit = nit != null && !nit.trim().isEmpty();
        boolean tieneNrc = nrc != null && !nrc.trim().isEmpty();

        cmbTipoDocumento.addItem("Factura Consumidor Final");

        if (tieneNit && tieneNrc) {
            cmbTipoDocumento.addItem("Comprobante de Credito Fiscal");
        }

        cmbTipoDocumento.setSelectedItem("Factura Consumidor Final");

        calcularTotal();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtFechaEmision = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtFechaVencimiento = new javax.swing.JTextField();
        txtEmpresa = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtIdFactura = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbTipoDocumento = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnAgregarCliente = new javax.swing.JButton();
        txtNumeroDocumento = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cmbProducto = new javax.swing.JComboBox<>();
        btnAgregar = new javax.swing.JButton();
        btnBuscarProducto = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txtSaldoPendiente = new javax.swing.JTextField();
        txtMontoExtenso = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        txtPrecioIVA = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtMontoGravado = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        btnProcesarCompra = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtEstadoVenta = new javax.swing.JTextField();
        cmbCategoriaProducto = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txtEfectivo = new javax.swing.JTextField();
        txtExistencias = new javax.swing.JTextField();
        cmbCliente = new javax.swing.JComboBox<>();
        btnLimpiar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();

        jLabel1.setText("Ventas");

        jLabel2.setText("Empresa");

        jLabel7.setText("Fecha");

        txtFechaEmision.setEditable(false);

        jLabel14.setText("Fecha Vencimiento");

        txtEmpresa.setEditable(false);
        txtEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpresaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFechaEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(txtFechaVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addComponent(txtFechaEmision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txtFechaVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jLabel4.setText("IdFactura");

        txtIdFactura.setEditable(false);

        jLabel5.setText("Tipo Documento");

        cmbTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Seleccione>", "Factura Consumidor Final", "Comprobante de Credito Fiscal" }));
        cmbTipoDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoDocumentoActionPerformed(evt);
            }
        });

        jLabel6.setText("Numero Documento");

        jLabel3.setText("Cliente");

        btnAgregarCliente.setText("AgregarCliente");
        btnAgregarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarClienteActionPerformed(evt);
            }
        });

        txtNumeroDocumento.setEditable(false);

        jLabel9.setText("Categoria Producto");

        jLabel17.setText("Productos");

        cmbProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProductoActionPerformed(evt);
            }
        });

        btnAgregar.setText("Agregar Producto");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnBuscarProducto.setText("BuscarProducto");
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        jLabel16.setText("Saldo Pendiente");

        txtSaldoPendiente.setEditable(false);

        txtMontoExtenso.setEditable(false);
        txtMontoExtenso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoExtensoActionPerformed(evt);
            }
        });

        jLabel11.setText("Monto Gravado");

        txtPrecio.setEditable(false);
        txtPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioActionPerformed(evt);
            }
        });

        txtPrecioIVA.setEditable(false);

        jLabel12.setText("Monto IVA");

        txtMontoGravado.setEditable(false);
        txtMontoGravado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMontoGravadoKeyReleased(evt);
            }
        });

        jLabel13.setText("Monto Exento");

        btnProcesarCompra.setText("Procesar Venta");
        btnProcesarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesarCompraActionPerformed(evt);
            }
        });

        jLabel10.setText("Precio Total");

        jLabel18.setText("Estado Venta");

        txtEstadoVenta.setEditable(false);

        cmbCategoriaProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoriaProductoActionPerformed(evt);
            }
        });

        jLabel8.setText("Dinero Recibido");

        txtEfectivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEfectivoActionPerformed(evt);
            }
        });
        txtEfectivo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEfectivoKeyReleased(evt);
            }
        });

        txtExistencias.setEditable(false);

        cmbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClienteActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(cmbTipoDocumento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(btnBuscarProducto)
                                    .addComponent(btnAgregar)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel17))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(cmbProducto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbCategoriaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addComponent(txtExistencias, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnProcesarCompra)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLimpiar)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSaldoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel18)
                                .addGap(23, 23, 23)
                                .addComponent(txtEstadoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                    .addComponent(jLabel10)
                                                    .addGap(28, 28, 28))
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel13)
                                                        .addComponent(jLabel12))
                                                    .addGap(17, 17, 17)))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtMontoExtenso)
                                    .addComponent(txtMontoGravado)
                                    .addComponent(txtPrecioIVA, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPrecio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEfectivo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtIdFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 291, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNumeroDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgregarCliente)))))
                .addGap(14, 14, 14))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAgregarCliente)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtIdFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(txtExistencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtNumeroDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtEstadoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtSaldoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMontoGravado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMontoExtenso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrecioIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cmbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(cmbCategoriaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(cmbProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addComponent(btnBuscarProducto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAgregar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(btnProcesarCompra)
                        .addComponent(btnLimpiar))
                    .addComponent(txtEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tblVentas);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnProcesarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesarCompraActionPerformed
        // 1. Validar productos
        if (modeloDetalle == null || modeloDetalle.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "No puede procesar una venta sin productos.",
                    "Detalle vacío",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. Validar cliente
        if (cmbCliente.getSelectedIndex() <= 0 || listaClientes == null) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un cliente.",
                    "Cliente requerido",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 3. Validar tipo documento
        if (cmbTipoDocumento.getSelectedItem() == null
                || cmbTipoDocumento.getSelectedItem().toString().trim().equals("<Seleccione>")
                || cmbTipoDocumento.getSelectedItem().toString().trim().equals("<Seleccione>")) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un tipo de documento válido.",
                    "Documento requerido",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 4. Recalcular totales antes de guardar
        calcularTotal();

        double totalVenta;
        double dineroRecibido = 0.0;

        try {
            totalVenta = Double.parseDouble(txtPrecio.getText().trim());

            if (!txtEfectivo.getText().trim().isEmpty()) {
                dineroRecibido = Double.parseDouble(txtEfectivo.getText().trim());
            }

        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "El total o el dinero recibido no es válido.",
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (totalVenta <= 0) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "El total de la venta debe ser mayor a cero.",
                    "Total inválido",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (dineroRecibido < 0) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "El dinero recibido no puede ser negativo.",
                    "Dinero inválido",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (dineroRecibido > totalVenta) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "El dinero recibido no puede ser mayor al total.",
                    "Dinero inválido",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double saldoPendiente = totalVenta - dineroRecibido;

        // 5. Fechas
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate fechaEmision;
        LocalDate fechaVencimiento = null;

        try {
            fechaEmision = LocalDate.parse(txtFechaEmision.getText().trim(), formato);
        } catch (DateTimeParseException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "La fecha de emisión no es válida.",
                    "Fecha inválida",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (saldoPendiente > 0) {

            String textoFechaVencimiento = txtFechaVencimiento.getText().trim();

            if (textoFechaVencimiento.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Debe ingresar fecha de vencimiento cuando la venta queda pendiente o parcial.",
                        "Fecha requerida",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                fechaVencimiento = LocalDate.parse(textoFechaVencimiento, formato);
            } catch (DateTimeParseException e) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "La fecha de vencimiento debe tener formato dd/MM/yyyy.",
                        "Formato inválido",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!fechaVencimiento.isAfter(LocalDate.now())) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "La fecha de vencimiento debe ser mayor a la fecha actual.",
                        "Fecha inválida",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        try {
            // 6. Cliente
            Cliente clienteSeleccionado = listaClientes.get(cmbCliente.getSelectedIndex() - 1);

            // 7. Tipo documento para BD
            String tipoSeleccionado = cmbTipoDocumento.getSelectedItem().toString().trim();
            String tipoDocumentoBD;

            if (tipoSeleccionado.toLowerCase().contains("credito")) {
                tipoDocumentoBD = "CCF";
            } else {
                tipoDocumentoBD = "FC";
            }

// 8. Crear venta
            Venta venta = new Venta();

            venta.setIdEntidad(clienteSeleccionado.getIdEntidad());
            venta.setTipoDocumento(tipoDocumentoBD);
            venta.setNumeroDocumento(txtNumeroDocumento.getText().trim());
            venta.setFechaEmision(fechaEmision);
            venta.setFechaVencimiento(fechaVencimiento);

            venta.setMontoGravado(Double.parseDouble(txtMontoGravado.getText().trim()));
            venta.setMontoIVA(Double.parseDouble(txtPrecioIVA.getText().trim()));
            venta.setMontoExento(Double.parseDouble(txtMontoExtenso.getText().trim()));
            venta.setMontoTotal(totalVenta);
            venta.setSaldoPendiente(saldoPendiente);

            if (saldoPendiente <= 0) {
                venta.setEstadoPago("PAGADO");
            } else if (dineroRecibido == 0) {
                venta.setEstadoPago("PENDIENTE");
            } else {
                venta.setEstadoPago("PARCIAL");
            }

            // 9. Crear detalles
            List<DetalleVenta> detalles = new ArrayList<>();

            for (int i = 0; i < modeloDetalle.getRowCount(); i++) {

                DetalleVenta detalle = new DetalleVenta();

                detalle.setIdProducto(Integer.parseInt(modeloDetalle.getValueAt(i, 2).toString()));
                detalle.setNombreProducto(modeloDetalle.getValueAt(i, 3).toString());
                detalle.setPrecioUnitario(Double.parseDouble(modeloDetalle.getValueAt(i, 4).toString()));
                detalle.setCantidad(Integer.parseInt(modeloDetalle.getValueAt(i, 5).toString()));
                detalle.setSubtotal(Double.parseDouble(modeloDetalle.getValueAt(i, 6).toString()));

                detalles.add(detalle);
            }

            // 10. Debug
            System.out.println("===== DATOS VENTA =====");
            System.out.println("Cliente ID: " + venta.getIdEntidad());
            System.out.println("Tipo doc: " + venta.getTipoDocumento());
            System.out.println("Número doc: " + venta.getNumeroDocumento());
            System.out.println("Fecha emisión: " + venta.getFechaEmision());
            System.out.println("Fecha vencimiento: " + venta.getFechaVencimiento());
            System.out.println("Total: " + venta.getMontoTotal());
            System.out.println("Saldo: " + venta.getSaldoPendiente());
            System.out.println("Estado: " + venta.getEstadoPago());
            System.out.println("Detalles: " + detalles.size());

            // 11. Guardar
            int idFacturaGenerada = ventaDAO.procesarVenta(venta, detalles);

            if (idFacturaGenerada > 0) {

                txtIdFactura.setText(String.valueOf(idFacturaGenerada));

                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Venta procesada correctamente.\nFactura ID: " + idFacturaGenerada,
                        "Venta guardada",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
                limpiarVenta();

            } else {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "No se pudo procesar la venta. El DAO devolvió: " + idFacturaGenerada,
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Error real al procesar venta:\n" + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnProcesarCompraActionPerformed

    private void txtMontoExtensoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoExtensoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMontoExtensoActionPerformed

    private void cmbProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProductoActionPerformed
        actualizarStockDisponible(); // Si cambia de Laptop a Mouse, el stock cambia en pantalla
    }//GEN-LAST:event_cmbProductoActionPerformed

    private void cmbCategoriaProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoriaProductoActionPerformed
        cargarProductosPorCategoria();
    }//GEN-LAST:event_cmbCategoriaProductoActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed

        if (cmbCliente.getSelectedIndex() <= 0) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un cliente antes de agregar productos.",
                    "Cliente requerido",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (cmbTipoDocumento.getSelectedItem() == null
                || cmbTipoDocumento.getSelectedItem().toString().trim().equals("<Seleccione>")
                || cmbTipoDocumento.getSelectedItem().toString().trim().equals("<Seleccione>")) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un tipo de documento antes de agregar productos.",
                    "Tipo de documento requerido",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int stockActual = Integer.parseInt(txtExistencias.getText());
        if (stockActual <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "No puedes agregar este producto, no hay existencias disponibles.");
            return;
        }

        // CANDADO NUEVO: Validar si seleccionó un tipo de documento válido
        if (cmbTipoDocumento.getSelectedItem() == null
                || cmbTipoDocumento.getSelectedItem().toString().equals("<Seleccione>")) {

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un Tipo de Documento válido antes de agregar productos.",
                    "Advertencia",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return; // Rompe el método aquí y no deja pasar al código de abajo
        }

        String productoSeleccionado = (String) cmbProducto.getSelectedItem();

        if (productoSeleccionado == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto.");
            return;
        }

        Productos productoEncontrado = null;
        if (listaProductos != null) {
            for (Productos prod : listaProductos) {
                if (prod.getNombreProducto().equals(productoSeleccionado)) {
                    productoEncontrado = prod;
                    break;
                }
            }
        }

        if (productoEncontrado != null) {
            int idBuscado = productoEncontrado.getIdProducto();
            boolean existeEnTabla = false;
            int filaDondeExiste = -1;

            // 1. ESCANEO: Buscamos si el ID ya está en la columna 2 de la tabla
            for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
                int idEnTabla = Integer.parseInt(modeloDetalle.getValueAt(i, 2).toString()); // Columna 2 es el ID
                if (idEnTabla == idBuscado) {
                    existeEnTabla = true;
                    filaDondeExiste = i;
                    break; // Lo encontramos, salimos del bucle de búsqueda
                }
            }

            // 2. DECISIÓN: Actuamos según el resultado del escaneo
            if (existeEnTabla) {
                // CAMINO A: Ya existe, así que sumamos 1 a la cantidad actual
                int cantidadActual = Integer.parseInt(modeloDetalle.getValueAt(filaDondeExiste, 5).toString()); // Columna 5 es Cantidad
                int nuevaCantidad = cantidadActual + 1;

                if (nuevaCantidad > productoEncontrado.getExistencias()) {
                    javax.swing.JOptionPane.showMessageDialog(
                            this,
                            "No hay existencias suficientes para agregar más unidades.",
                            "Stock insuficiente",
                            javax.swing.JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Al actualizar la celda, tu TableModelListener se despertará solo,
                // recalculará el subtotal de esa fila y mandará a llamar a calcularTotal().
                modeloDetalle.setValueAt(nuevaCantidad, filaDondeExiste, 5);

            } else {
                // CAMINO B: No existe, insertamos la fila como un producto nuevo
                int numeroLinea = modeloDetalle.getRowCount() + 1;
                double precioReal = productoEncontrado.getPrecioVenta();
                String tipo = "PROD";
                int cantidadInicial = 1;
                double subtotal = precioReal * cantidadInicial;

                modeloDetalle.addRow(new Object[]{
                    numeroLinea, // Columna 0
                    tipo, // Columna 1
                    idBuscado, // Columna 2
                    productoEncontrado.getNombreProducto(), // Columna 3
                    precioReal, // Columna 4
                    cantidadInicial, // Columna 5
                    subtotal // Columna 6
                });

                // Como fue una fila nueva hecha por código, forzamos el cálculo del total general
                calcularTotal();
                actualizarStockDisponible();
            }

        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al recuperar los datos del producto.");
        }

        //Al final del método agregas esto:
        calcularTotal();

    }//GEN-LAST:event_btnAgregarActionPerformed

    private void txtEfectivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEfectivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEfectivoActionPerformed

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void txtMontoGravadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMontoGravadoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMontoGravadoKeyReleased

    private void txtEfectivoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEfectivoKeyReleased
        calcularTotal();
    }//GEN-LAST:event_txtEfectivoKeyReleased

    private void cmbTipoDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoDocumentoActionPerformed
        if (cmbTipoDocumento.getSelectedItem() != null) {

            String tipoSeleccionado = cmbTipoDocumento.getSelectedItem().toString().trim();

            if (!tipoSeleccionado.equals("<Seleccione>")
                    && !tipoSeleccionado.equals("<Seleccione>")) {

                String numero = ventaDAO.generarNumeroDocumento(tipoSeleccionado);
                txtNumeroDocumento.setText(numero);
            } else {
                txtNumeroDocumento.setText("");
            }
        }

        calcularTotal();
    }//GEN-LAST:event_cmbTipoDocumentoActionPerformed

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed
        long idEmpresa = 1L; // fallback sin login
        if (com.sistema.core.security.SessionManager.getInstancia().haySesionActiva()) {
            idEmpresa = com.sistema.core.security.SessionManager.getInstancia().getIdEmpresa();
        }
        JDBuscarProducto dialogo = new JDBuscarProducto(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                true, (int) idEmpresa);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);


    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void btnAgregarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClienteActionPerformed

        javax.swing.JFrame ventanaCliente = new javax.swing.JFrame("Agregar Cliente");
        JPCliente panelCliente = new JPCliente();

        ventanaCliente.setContentPane(panelCliente);
        ventanaCliente.setSize(900, 600);
        ventanaCliente.setLocationRelativeTo(this);
        ventanaCliente.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

        ventanaCliente.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cargarClientes();

            }
        });

        ventanaCliente.setVisible(true);
    }//GEN-LAST:event_btnAgregarClienteActionPerformed

    private void cmbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClienteActionPerformed

        actualizarTipoDocumentoPorCliente();

    }//GEN-LAST:event_cmbClienteActionPerformed

    private void txtEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpresaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmpresaActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarVenta();        // TODO add your handling code here:
    }//GEN-LAST:event_btnLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAgregarCliente;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnProcesarCompra;
    private javax.swing.JComboBox<String> cmbCategoriaProducto;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JComboBox<String> cmbProducto;
    private javax.swing.JComboBox<String> cmbTipoDocumento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblVentas;
    private javax.swing.JTextField txtEfectivo;
    private javax.swing.JTextField txtEmpresa;
    private javax.swing.JTextField txtEstadoVenta;
    private javax.swing.JTextField txtExistencias;
    private javax.swing.JTextField txtFechaEmision;
    private javax.swing.JTextField txtFechaVencimiento;
    private javax.swing.JTextField txtIdFactura;
    private javax.swing.JTextField txtMontoExtenso;
    private javax.swing.JTextField txtMontoGravado;
    private javax.swing.JTextField txtNumeroDocumento;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtPrecioIVA;
    private javax.swing.JTextField txtSaldoPendiente;
    // End of variables declaration//GEN-END:variables
}
