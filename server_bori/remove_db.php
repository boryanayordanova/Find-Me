<?php  

	require "init.php";  


	$sql_drop_statements = array(

			/* FOR USERS */
			"DROP TABLE USERS;",

			/* FOR COORDINATES */
			"DROP TABLE COORDINATES;",

			/* FOR RELATIONS */
			"DROP TABLE RELATIONS;"

		);

	$flag_success = true;
	foreach ($sql_drop_statements as $sql_statement) {
		
		if(!mysqli_query($conn, $sql_statement)){
			$flag_success = false;
		}
	}

	if($flag_success == true){

		$responce = array(
			"success" => true
		);

	} else {
		$responce = array(
			"success" => false,
			"error" => "Problem dropping all tables"
		);

	}
	echo json_encode($responce);
 
?>  	