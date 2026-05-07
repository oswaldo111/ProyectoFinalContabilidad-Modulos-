SELECT
c.codigo,
c.nombre,
SUM(dp.debe) AS total_debe,
SUM(dp.haber) AS total_haber,
SUM(dp.debe - dp.haber) AS saldo,
FROM cuentas c
INNER JOIN detalles_partida dp
ON c.id_cuenta=dp.id_cuenta
GROUP BY c.codigo, c.nombre
ORDER BY c.codigo;
