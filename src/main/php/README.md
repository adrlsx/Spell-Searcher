# Web interface

Once the server database has been initialized, a web interface is available to search for spells. An example of it without the pictures is available [there](https://azura-levidre.000webhostapp.com/spellsv2.php). Please be aware that MySQL could be down.
To update the database with a new creature.jsonl or a new spell.jsonl, it's [here](https://azura-levidre.000webhostapp.com/bdd-envoi.php).

## How to use

The user selects the options in a form and send it. By default, all the options are set on Anything, which means it will select every spell. The page then shows the spells according to the form the user fulfilled.

The user may want a specific spell. He can write some keywords in the search box to only display spells with those keywords in their description.

The web interface will display the spell list with their corresponding card picture, name, and the creatures able to learn it, if there are some. The spell name, and the creatures ones are linked to their [Archives of Nethys](https://www.aonprd.com/) pages.

## Technology

The form is in HTML and is sent with a post to the same page. The PHP code will retrieve it and connect to the database, then it creates the SQL request and send it. It retrieves the response and verify it corresponds to the type selected in the form and the possible keywords. It will resend a new request to the database asking for the creatures learning each spell and display the spells.
The database itself is MySQL.
