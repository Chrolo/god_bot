<?php
//Get the connection "$db_con":
require("DatabaseConnector.php");

//Get database definitions:
require("databaseDefinition.php");


if ($argv[1]=='--debug')
	$debug = true;
else
	$debug = false;


//Process each table of the database:
foreach($databaseDefinition[tables] as $tableName => $tableStructure)
{
	CreateDatabaseTable($tableName, $tableStructure[columns]);
}
exit;
//-------------------------------------------------------------------------
// END OF SCRIPT, HERE BE FUNCTIONS:
//--------------------------------------------------------------------------
function CreateDatabaseTable($tableName, $tableStructure)
{
	echo "Checking database for $tableName\n";
	$exists = mysqli_query($GLOBALS[db_con],"DESCRIBE `".$tableName."`;");
	if($exists)
	{
		if($GLOBALS[debug])
		{
			echo "Found a table that was:\n";
			for($i=0;$i<mysqli_num_rows($exists);$i++)
			{
				//Get the data:
				foreach(mysqli_fetch_assoc($exists) as $key=>$val)
				{
					echo "\t$val";
				}
				echo "\n";
			}
			echo "\n";
		}


		//Check structure of Database:
		$flag=true;

		//create prepared Statement:
		$statement = "SHOW COLUMNS IN `".$tableName."` WHERE Field= ? AND Type= ?";
		$query = $GLOBALS[db_con]->prepare($statement);
		if($query === false)
		{
			echo "Error creating prepared statment:\nStatement was:\t".$statement."\nSQL error:\t".$GLOBALS[db_con]->error;
			return 0;
		}

		foreach($tableStructure as $fieldName => $fieldProps)
		{
			$type = $fieldProps[type];

			$query->bind_param('ss', $fieldName, $type);

			if($query->execute())
			{
				//Count results manually (because num_rows is ~a peice of shit~ only valid for SELECT statements....)
				$res_count = 0;
				$cols = [];
				for($i=0;$i<$query->field_count;$i++){$cols[]=" ";};
				$query->bind_result(...$cols);
				while ($query->fetch()) {
					$res_count++;
					if($GLOBALS[debug])
						print_r($cols);
				}
				if( $res_count !== 1 )	//if( $query->num_rows < 1 )	//Changed to "if I can fetch a row". Because apparently num_row is only valid for SELECT statements....
				{
					$flag = false;
					echo "\t\"".$fieldName."\" was either missing or inappropriate.\n";
				}
			}
			else
			{
				$flag = false;
				echo "[ERROR] SQL returned false to query `".$query->error."`\n";
			}
		}
		$query->close();

		if(!$flag) //if the table doesn't have all the required feilds
		{
			//drop the old table (because it's simpler)
			if(mysqli_query($GLOBALS[db_con],"DROP table ".$tableName.";"))
			{
				echo "Dropped table ".$tableName."\n";
			}
			else
			{
				echo "[ERROR] failed to drop table!: ".mysqli_error($GLOBALS[db_con])."\n";
			}
			$exists=false;
		}
		else
		{
			echo "Existing table `$tableName` is adequate\n";
		}
	}

    if (!$exists)//if the table doesn't exist
	{
		//create table
			//create sql for table creation
		$sql='CREATE TABLE `'.$tableName.'` ( ';
		$flag = false;
		foreach($tableStructure as $key => $val)
		{
			if($flag)
				$sql.=", ";
			$flag = true;

			$sql.=$key.' '.$val[type].' ';

			if(isset($val[extra]))
				$sql .= $val[extra];

			if($val[primaryKey]===true)
				$sql_end=', PRIMARY KEY ('.$key.')';

		}
		$sql .= $sql_end.');';

		//create table:
		if(mysqli_query($GLOBALS[db_con],$sql))
		{
			echo "New table `$tableName` created\n";
			if($GLOBALS[debug])
			{
				echo "\t...using the SQL: $sql\n";
			}
			$new_table_flag=true;
		}
		else
		{
			echo "[ERROR] Error creating table `".$tableName."`: ".mysqli_error($GLOBALS[db_con])."\nSQL was: $sql\n";
		}
	}
	echo "\n";
}

?>
