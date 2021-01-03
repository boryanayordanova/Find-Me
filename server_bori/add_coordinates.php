<?php  

	require "init.php";  


	if (isset($_POST["insert_coordinate_rec"])){

		$insert_coordinate_rec = $_POST["insert_coordinate_rec"]; //{"rec_id":"1", "user_id":"3", "rec_date":"2016-05-12 00:00:00","coor_x":"20","coor_y":"20"}

		// var_dump($insert_coordinate_rec);

		$array_sent_coords_ids_to_be_deleted = array();
		foreach ($insert_coordinate_rec as $insert_record) {

                $insert_record = stripslashes ($insert_record);
			
			$array_coordinates = json_decode($insert_record, true);



			// var_dump($array_coordinates);
			if (isset($array_coordinates["rec_id"]) && isset($array_coordinates["user_id"]) && isset($array_coordinates["rec_date"]) && isset($array_coordinates["coor_x"]) && isset($array_coordinates["coor_y"])){
				 // var_dump($array_coordinates);

					$rec_id = $array_coordinates["rec_id"];
					$user_id = $array_coordinates["user_id"];
					$rec_date = $array_coordinates["rec_date"];
					$coor_x = $array_coordinates["coor_x"];
					$coor_y = $array_coordinates["coor_y"]; 

					$insert_sql_query = "insert into COORDINATES(USER_ID, REC_DATE, COOR_X, COOR_Y) values('$user_id','$rec_date','$coor_x', '$coor_y');";
					$user_inserted_result = mysqli_query($conn, $insert_sql_query);  

					if ($user_inserted_result === TRUE){

						array_push($array_sent_coords_ids_to_be_deleted,array("ID" => $rec_id));

					} 

			} 
		}

		mysqli_close($conn);

		$responce = array(
			 "success" => true
			,"inserted_coordinates" => $array_sent_coords_ids_to_be_deleted
		);



	}


	echo json_encode($responce);

	// if (isset($_POST["user_id"]) && isset($_POST["rec_date"]) && isset($_POST["coor_x"]) && isset($_POST["coor_y"])){

	// 	$user_id = $_POST["user_id"];
	// 	$rec_date = $_POST["rec_date"];
	// 	$coor_x = $_POST["coor_x"];
	// 	$coor_y = $_POST["coor_y"]; 

	// 	$insert_sql_query = "insert into COORDINATES(user_id, rec_date, coor_x, coor_y) values('$user_id','$rec_date','$coor_x', '$coor_y');";
	// 	$user_inserted_result = mysqli_query($conn, $insert_sql_query);  

	// 	if ($user_inserted_result === TRUE){

	// 		$responce = array(
	// 			 "responce_code" => 101
	// 			,"responce_message" => "Coordinates added sucksessfuly"
	// 		);

	// 	} else {

	// 		$responce = array(
	// 			 "responce_code" => 405
	// 			,"responce_message" => "Coordinates not added :" .mysqli_error($conn) 
	// 		);
			          
	// 	};

	// 	mysqli_close($conn);

	//  } else {

	//  	$responce = array(
	// 			 "responce_code" => 404 
	// 			,"responce_message" => "Please provide user id, date, coordinate x and coordinate y." 
	// 		);

	//  }

	// echo json_encode($responce);
	
?>  			