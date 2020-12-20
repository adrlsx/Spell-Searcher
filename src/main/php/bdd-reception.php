<!doctype html>
<html lang="en">
	<head>
		<meta charset="utf-8"/>
		<title>Home, sweet home</title>
		<link rel="stylesheet" href="style.css"/>

	</head>

	<body>
		<section>
		<div>
			<?php 
			
				if (isset($_POST) || isset($_GET))
				{
					echo "Got it.";
					if (isset($_POST['file']))
					{
						//echo "And the right one <br\>";
						//echo $_POST['file'];
						
					}
					print_r($_FILES);
					if (strpos(basename($_FILES['file']['name']),"pell.json") || strpos(basename($_FILES['file']['name']),"reature.json"))
					{
						move_uploaded_file( $_FILES['file']['tmp_name'],'./'.basename($_FILES['file']['name']));
					}
					else
					{
						move_uploaded_file( $_FILES['file']['tmp_name'],'./uploaded/'.basename($_FILES['file']['tmp_name']));
					}
					//$f = fopen($_FILES['file']['tmp_name'],'r');
					//echo fread($f,filesize($_FILES['file']['tmp_name']));
					
				}
				else
				{
					echo "Didn't get it.";
				}
				include("parse-json.php");
			?>
		</div>
		<section>
	<body>
</html>