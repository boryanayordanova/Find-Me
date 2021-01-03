<?php  

	require "init.php";  


	$sql_create_statements = array(

			/* FOR USERS */
			"CREATE TABLE IF NOT EXISTS USERS (
			    ID                  INT(8) UNSIGNED AUTO_INCREMENT NOT NULL,
			    NAME                VARCHAR(100) NOT NULL UNIQUE,
			    PASSWORD            VARCHAR(60) NOT NULL,
			    DATE_REGISTERES     DATETIME NOT NULL,
			    PRIMARY KEY(ID)
			)
			ENGINE = InnoDB
			DEFAULT CHARACTER SET = cp1251;",

			/* FOR COORDINATES */
			"CREATE TABLE IF NOT EXISTS COORDINATES (
			    ID                  INT(11) UNSIGNED AUTO_INCREMENT NOT NULL,
			    USER_ID             INT(8) UNSIGNED NOT NULL,
			    REC_DATE            DATETIME NOT NULL,
			    COOR_X              NUMERIC(12,8) NOT NULL,
			    COOR_Y              NUMERIC(12,8) NOT NULL,
			    PRIMARY KEY (ID),
			    CONSTRAINT FK_COORDINATES_USER_ID FOREIGN KEY (USER_ID) REFERENCES USERS(ID)
			)
			ENGINE = InnoDB
			DEFAULT CHARACTER SET = cp1251;",

			/* FOR RELATIONS */
			"CREATE TABLE IF NOT EXISTS RELATIONS (
			    ID                  INT(11) UNSIGNED AUTO_INCREMENT NOT NULL,
			    USER_ID_ONE         INT(8) UNSIGNED NOT NULL,
			    USER_ID_TWO         INT(8) UNSIGNED NOT NULL,
			    REQUEST_STATUS		int(1) UNSIGNED NOT NULL,
			    PRIMARY KEY (ID),
			    CONSTRAINT FK_RELATIONS_USER_ID_ONE FOREIGN KEY (USER_ID_ONE) REFERENCES USERS(ID),
			    CONSTRAINT FK_RELATIONS_USER_ID_TWO FOREIGN KEY (USER_ID_TWO) REFERENCES USERS(ID)
			)
			ENGINE = InnoDB
			DEFAULT CHARACTER SET = cp1251;"

		);

	$flag_success = true;

	foreach ($sql_create_statements as $sql_statement) {
		
		if(!mysqli_query($conn, $sql_statement)){
			$flag_success = false;
		} 
	}

	if($flag_success === true){

		$responce = array(
			"success" => true
		);

	} else {
		$responce = array(
			"success" => false,
			"error" => "Problem creating all tables"
		);

	}
 
	echo json_encode($responce);
?>  	