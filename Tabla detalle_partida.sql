Create table detalle_partida(
id_detalle Int primary key
AUTO_INCREMENT,
id_partida Int,
id_cuente Int,
debe DECIMAL(10,2),
haber DECIMAL(10,2),

FOREIGN KEY(id_partida) REFERENCES partidas(id_partida);
FOREIGN KEY(id_cuenta) REFERENCES partidas(id_cuenta);
);