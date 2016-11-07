<?php

//Get the connection "$db_con":
require("DatabaseConnector.php");

//Get database definitions:
require("databaseDefinition.php");

//Get testData:
require("testData.php");

//Add the data:
foreach($TestData as $tableName => $testData)
{
	addDatatoTable($tableName, $databaseDefinition[tables][$tableName][columns], $testData, true);
}



//---------------------------------------------------------------------------------------
//	Functions
//---------------------------------------------------------------------------------------

function addDatatoTable($tableName, $tableStructure, $dataToInsert, $truncateFirst = false)
{
	//Truncate table first?
	if($truncateFirst)
	{
		if(!mysqli_query($GLOBALS[db_con],"TRUNCATE `$tableName`;"))
			echo "[ERROR] Table `$tableName` could not be truncated.\n";
	}

	//Figure out what fields are going in:
	$fieldStr = "";		// The fields we're inserting
	$params = "";		// This is a string that will have to be duplicated into the statement for each line we're adding.

	$flag=false;

	$values = array(); //The values that will need to be bound.
	$param_types="";	// The parameters of the fields. Used in query->bind_param();


	//fields to update:
	$flag = false;
	foreach($tableStructure as $key => $val)
	{
		if($flag) // we add comma before each field, but not before the first one.
			$fieldStr.=", ";
		$flag=TRUE;

		$fieldStr .="`".$key."`";
	}

	//Construct data dependent variables:
	$firstRow = true;
	$params = "(";
	foreach($dataToInsert as $line_array)
	{
		if(!$firstRow)
			$params.="), (";
		$flag = false;
		foreach($tableStructure as $key => $val)
		{

			if($flag) // we add comma before each field, but not before the first one.
			{
				$params.=", ";
			}
			$flag=TRUE;

			//For the prepared statement:
			$params .= "?";


			// For the bind_Params:
			//Set param_types
			switch($val['type'])
			{
				default:
					$param_types .="s";
					break;
				case 'INT( 11 )':
					$param_types .="i";
					break;
			}
			//Append the value:
			$values[] = $line_array[$key];


		}
		$firstRow = false;
	}
	$params.=")";


	//make the prepared statement.
	$statement = "INSERT INTO `".$tableName."` ( $fieldStr ) VALUES $params;";
	$query = $GLOBALS[db_con]->prepare($statement);
	if($query === false)
	{
		echo "Error creating prepared statment:\nStatement was:\t".$statement."\nSQL error:\t".$GLOBALS[db_con]->error;
		return 0;
	}

	//Run the query:
	//bind parameters:
	$query->bind_param($param_types,...$values);
	$query->execute();

	if($query->affected_rows != count($dataToInsert))
	{
		echo "Error inserting lines: \n Expected to insert ".count($dataToInsert)." but saw only ".$query->affected_rows."\n".$query->error."\n";
	}
	else
	{
		echo "Test Data successfully added to table $tableName\n";
	}

}
?>
