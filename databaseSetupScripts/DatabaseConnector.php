<?php
//Creates a database connection based on the details in the settings.ini
//This file is then included in the other database scripts.

//Get settings.ini:
$settings = parse_ini_file("../config/settings.ini",true);

$db_con=mysqli_connect($settings[database][hostname],$settings[database][createUser],$settings[database][createPassword],$settings[database][database]);
if (mysqli_connect_errno($db_con))
{
	echo "[DatabaseConnector] Failed to setup connection to MySQL: " . mysqli_connect_error($db_con);
	exit;
}
?>
