<!doctype html>
<html lang="en">
<head>
		<meta charset="utf-8"/>
		<title>Un site</title>
		<link rel="stylesheet" href="style.css"/>

</head>
<body>
<section>
<p>
<?php
	// Parse spell.json 
	$f = fopen("spell.jsonl","r");
	// On se connecte à la bdd
	$connection = mysqli_connect("127.0.0.1", "root", "", "spells", 3306);
	// On lit ligne par ligne
	while (($line = fgets($f))!=false)
	{
		// On récupère la ligne sous forme d'array
		$data = json_decode($line,true);
		// Création de la requête
		$req = "INSERT INTO `spellsv2` (`name`, `url`, `school`, `classes`,`components`,`spell_resistance`, `description`) values (\"".$data['name'].'", "'.$data['url'].'", "'.$data['school'].'", "';
		if ($data['classes']!="")
		{
			foreach ($data['classes'] as &$value)
			{
				$req.="|".$value."|";
			}
		}
		$req.='", "';
		unset($value);
		if ($data['components']!="")
		{
			foreach ($data['components'] as &$value)
			{
				$req.="|".$value."|";
			}
		}
		
		$req.='", "'.$data['spell_resistance'].'", "';
		foreach ($data['description'] as &$value)
		{
			$req.="|".$value."|";
		}
		$req.='") ON DUPLICATE KEY UPDATE `name`="'.$data['name'].'", `url`="'.$data['url'].'", `school`="'.$data['school'].'", `classes`="';
		unset($value);
		if ($data['classes']!="")
		{
			foreach ($data['classes'] as &$value)
			{
				$req.="|".$value."|";
			}
		}
		$req.='", `components`="';
		if ($data['components']!="")
		{
			foreach ($data['components'] as &$value)
			{
				$req.="|".$value."|";
			}
		}
		unset($value);
		$req.='", `spell_resistance`="'.$data['spell_resistance'].'", `description`="';
		foreach ($data['description'] as &$value)
		{
			$req.="|".$value."|";
		}
		unset($value);
		$req.='"';
		
		// On envoie les données pour remplir ou mettre à jour la bdd
		$res = mysqli_query($connection,$req);	
	}
	$f = fclose($f);



	// Parse creature.json 
	$f = fopen("creature.jsonl","r");
	// On lit ligne par ligne
	while (($line = fgets($f))!=false)
	{
		// On récupère la ligne sous forme d'array
		$data = json_decode($line,true);
		// Création de la requête
		$req = "INSERT INTO `creaturesv2` (`name`, `url`, `spells`, `description`) values (\"".$data['name'].'", "'.$data['url'].'", "';
		if ($data['spells']!="")
		{
			foreach ($data['spells'] as &$value)
			{
				$req.="|".$value."|";
			}
		}
		$req.='", "'.$data["description"].'") ON DUPLICATE KEY UPDATE `name`="'.$data['name'].'", `url`="'.$data['url'].'", `spells`="';
		unset($value);
		if ($data['spells']!="")
		{
			foreach ($data['spells'] as &$value)
			{
				$req.="|".$value."|";
			}
		}
		$req.='", `description`="'.$data["description"].'"';
		// On envoie les données pour remplir ou mettre à jour la bdd
		$res = mysqli_query($connection,$req);	
	}
$f = fclose($f);
echo "You can now test the searcher <a href='spellsv2.php'>here</a>."
?>

</p>
</section>
</body>
</html>