SELECT
	c.COLUMN_NAME AS "columnName",
	c.COLUMN_TYPE AS "columnType",
	c.DATA_TYPE AS "dataType",
	c.COLUMN_COMMENT AS "columnComment",
	c.COLUMN_KEY AS "columnKey",
	c.IS_NULLABLE AS "isNullable",
	c.EXTRA AS "extra"
FROM
	information_schema.`COLUMNS` c
WHERE
	c.TABLE_NAME = '%s' -- AND c.TABLE_NAME = (SELECT DATABASE())
	and table_schema = (select database())
ORDER BY
	c.ORDINAL_POSITION;