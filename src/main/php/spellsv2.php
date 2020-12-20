<!doctype html>
<html lang="en">
<head>
		<meta charset="utf-8"/>
		<title>Un site</title>
		<link rel="stylesheet" href="style.css"/>

</head>
<body>

<section>
<h2>Spell Searcher</h2>
<p>
<form action = "spellsv2.php" method="post">
	<!-- Le formulaire de recherche !-->
	<label for="keywords">Search keywords</label>
	<p><input type="text" name="keywords" placeholder="Keywords" autofocus></p>
	<label for="classe">Class selected</label>
	<p>
		<input type="radio" name="classe" value="Anything" checked /> Anything
		<input type="radio" name="classe" value="Alchemist" /> Alchemist
		<input type="radio" name="classe" value="Antipaladin" /> Antipaladin
		<input type="radio" name="classe" value="Arcanist" /> Arcanist
		<input type="radio" name="classe" value="Bard" /> Bard
		<input type="radio" name="classe" value="Bloodrager" /> Bloodrager
		<input type="radio" name="classe" value="Cleric" /> Cleric
		<input type="radio" name="classe" value="Druid" /> Druid
		<input type="radio" name="classe" value="Hunter" /> Hunter
		<input type="radio" name="classe" value="Inquisitor" /> Inquisitor
		<input type="radio" name="classe" value="Investigator" /> Investigator
		<input type="radio" name="classe" value="Magus" /> Magus
		<input type="radio" name="classe" value="Medium" /> Medium
		<input type="radio" name="classe" value="Mesmerist" /> Mesmerist
		<input type="radio" name="classe" value="Occultist" /> Occultist
		<input type="radio" name="classe" value="Oracle" /> Oracle
		<input type="radio" name="classe" value="Paladin" /> Paladin
		<input type="radio" name="classe" value="Psychic" /> Psychic
		<input type="radio" name="classe" value="Ranger" /> Ranger
		<input type="radio" name="classe" value="Redmantisassassin" /> Redmantisassassin
		<input type="radio" name="classe" value="Shaman" /> Shaman
		<input type="radio" name="classe" value="Skald" /> Skald
		<input type="radio" name="classe" value="Sorcerer" /> Sorcerer
		<input type="radio" name="classe" value="Spiritualist" /> Spiritualist
		<input type="radio" name="classe" value="Summoner" /> Summoner
		<input type="radio" name="classe" value="Warpriest" /> Warpriest
		<input type="radio" name="classe" value="Witch" /> Witch
		<input type="radio" name="classe" value="Wizard" /> Wizard
	</p>
	
	<label for="school">School selected</label>
	<p>
		<input type="radio" name="school" value="Anything" checked /> Anything
		<input type="radio" name="school" value="false" /> Out of school
		<input type="radio" name="school" value="Enchantment" /> Enchantment
		<input type="radio" name="school" value="Conjuration" /> Conjuration
		<input type="radio" name="school" value="Necromancy" /> Necromancy
		<input type="radio" name="school" value="Transmutation" /> Transmutation
		<input type="radio" name="school" value="Abjuration" /> Abjuration
		<input type="radio" name="school" value="Illusion" /> Illusion
		<input type="radio" name="school" value="Evocation" /> Evocation
		<input type="radio" name="school" value="Divination" /> Divination
		<input type="radio" name="school" value="Universal" /> Universal
	</p>
	<label for="type">Type selected</label>
	<p>
		<input type="radio" name="type" value="Anything" checked /> Anything
		<input type="radio" name="type" value="V" /> Verbal
		<input type="radio" name="type" value="S" /> Somatic
		<input type="radio" name="type" value="M" /> Material
	</p>
	
	<p>
		<button class="btn btn-lg btn-primary btn-block" type="submit" name="enter">Entrer</button>
	</p>
</form>

<?php
	//On se connecte à la base de donnée
	$connection = mysqli_connect("127.0.0.1", "root", "", "spells", 3306);
	// Création de la requête et élimination des lignes héritées du json utilisé pour le remplissage de la bdd
	$req = "SELECT * FROM `spellsv2` WHERE name != 'All classes' AND name!='name'";
	
	//On vérifie que le formulaire a bien été rempli puisqu'il ne le sera pas à la première visite de l'utilisateur
 	if (isset($_POST) && isset($_POST['classe']))
	{
		// On complète la requête avec ce que l'utilisateur a rentré dans le formulaire
		if ($_POST['classe'] != "Anything")
		{
			$req.=' AND `classes` LIKE "%|'.$_POST["classe"].'|%"';
		}

		if ($_POST['school'] != "Anything")
		{
			$req .= " AND `school` = '".$_POST['school']."'"; 
		}
		
		
		if ($_POST['type'] != "Anything")
		{
			$req.=' AND `components` LIKE "%|'.$_POST["type"].'|%"';
		}
		
		// La requête est envoyée
		$res = mysqli_query($connection,$req);
		
		// On récupère la réponse de la bdd
		while ($data = mysqli_fetch_assoc($res)) {
			
			if ($_POST['keywords'] == "")
			{
				$req2 = 'SELECT name,url FROM `creaturesv2` WHERE spells LIKE "%|'.$data["name"].'|%"';
				$res2 = mysqli_query($connection,$req2);
				$name = "";
				while ($data2 = mysqli_fetch_assoc($res2))
				{
					if ($name != "")
					{
						$name.=", ";
					}
					$name.="<a href='".$data2["url"]."'>".$data2["name"]."</a>";
				}
				// L'affichage change si des créatues apprennent le sort
				if ($name != "")
				{
					echo "<li><img class='spell-card' src='/spells/".str_replace("'","_",strtolower($data["name"])).".png'"." alt='A card ' height='250' /><a href='".$data["url"]."'>".$data["name"]."</a>, creatures : ".$name."</li>";
				}
				else
				{
					echo "<li><img class='spell-card' src='/spells/".str_replace("'","_",strtolower($data["name"])).".png'"." alt='A card ' height='250' /><a href='".$data["url"]."'>".$data["name"]."</a></li>";
				}
			}
			else
			{
				// On scan la description du sort et des mots-clés pour vérifier que ces derniers soient bien dedans
				$description = str_replace(array("||","|"),array(",",""),$data["description"]);
				$description = explode(",",$description);
				$keywords = explode(" ",$_POST['keywords']);
				//var_dump($keywords);
				//var_dump($description);
				$displayed = false;
				$found = 0;
				foreach ($keywords as $key)
				{
					foreach ($description as $descr)
					{
						if ($key==$descr)
						{
							$found +=1;
							//echo "<li><a href='".$data["url"].">".$data["name"]."</a></li>";
						}
					}
				}
				// On vérifie qu'on a bien trouvé tout les mots-clés dans la description du sort
				if ($found == count($keywords))
				{
					// On récupère les créatures capables d'apprendre le sort
					$req2 = 'SELECT name,url FROM `creaturesv2` WHERE spells LIKE "%|'.$data["name"].'|%"';
					$res2 = mysqli_query($connection,$req2);
					$name = "";
					while ($data2 = mysqli_fetch_assoc($res2))
					{
						if ($name != "")
						{
							$name.", ";
						}
						$name.="<a href='".$data2["url"]."'>".$data2["name"]."</a>";
					}
					// L'affichage change si des créatues apprennent le sort
					if ($name != "")
					{
						echo "<li><img class='spell-card' src='/spells/".str_replace("'","_",strtolower($data["name"])).".png'"." alt='A card ' height='250' /><a href='".$data["url"]."'>".$data["name"]."</a>, creatures : ".$name."</li>";
					}
					else
					{
						echo "<li><img class='spell-card' src='/spells/".str_replace("'","_",strtolower($data["name"])).".png'"." alt='A card ' height='250' /><a href='".$data["url"]."'>".$data["name"]."</a></li>";
					}
				}
			
			}
			
		}
		
		
	}

?></ul>
</p>
</section>
</body>
</html>