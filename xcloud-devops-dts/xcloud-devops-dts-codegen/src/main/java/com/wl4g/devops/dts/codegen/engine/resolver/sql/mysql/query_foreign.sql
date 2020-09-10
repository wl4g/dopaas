SELECT
	f.ID AS "id",
	SUBSTRING_INDEX(f.ID, '/', 1) AS "dbName",
	SUBSTRING_INDEX(f.FOR_NAME, '/', - 1) AS "forTableName",
	SUBSTRING_INDEX(f.REF_NAME, '/', - 1) AS "refTableName",
	fc.FOR_COL_NAME AS "forColumnName",
	fc.REF_COL_NAME AS "refColumnName"
FROM
	information_schema.INNODB_SYS_FOREIGN f
LEFT JOIN information_schema.INNODB_SYS_FOREIGN_COLS fc ON fc.ID = f.ID
WHERE
	SUBSTRING_INDEX(f.ID, '/', 1) = '%s'
AND SUBSTRING_INDEX(f.FOR_NAME, '/', - 1) = '%s';