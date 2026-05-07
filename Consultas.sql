SELECT
    c.nombre,
    c.tipo,
    SUM(dp.haber - dp.debe) AS total
FROM cuentas c
INNER JOIN detalle_partida dp
ON c.id_cuenta = dp.id_cuenta
WHERE c.tipo IN ('INGRESO', 'GASTO')
GROUP BY c.nombre, c.tipo;