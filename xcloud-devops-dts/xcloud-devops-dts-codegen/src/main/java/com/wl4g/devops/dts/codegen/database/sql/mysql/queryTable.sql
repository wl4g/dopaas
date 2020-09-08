SELECT
	t.TABLE_NAME AS "tableName",
	t.`ENGINE` AS "engine",
	t.TABLE_COMMENT AS "tableComment",
	t.CREATE_TIME AS "createTime"
FROM
	information_schema.`TABLES` t
WHERE
	table_schema = (SELECT DATABASE())
AND table_name = '%d';