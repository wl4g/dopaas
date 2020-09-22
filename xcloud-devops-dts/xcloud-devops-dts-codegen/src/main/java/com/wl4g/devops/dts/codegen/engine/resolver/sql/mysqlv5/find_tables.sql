SELECT
	t.TABLE_SCHEMA AS "tableSchema",
	t.TABLE_NAME AS "tableName",
	t.`ENGINE` AS "engine",
	t.TABLE_COMMENT AS "tableComment",
	t.CREATE_TIME AS "createTime"
FROM
	information_schema.`TABLES` t
WHERE
	t.TABLE_SCHEMA = (SELECT DATABASE())
AND t.TABLE_TYPE = 'BASE TABLE'
AND t.TABLE_NAME LIKE '%%s%';