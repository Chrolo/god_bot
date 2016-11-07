<?php

$DB_Json = file_get_contents('../config/databaseDefinitions.json');

$databaseDefinition = json_decode($DB_Json, true);

//Check the DB_array was initialised:
if($databaseDefinition == null)
{
	echo "[JSON Error] '../config/databaseDefinitions.json' couldn't be parsed as json. Check for syntax errors";
	exit;
}

?>
