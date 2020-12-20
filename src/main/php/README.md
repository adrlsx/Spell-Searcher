# Web interface

Once the server database has been initialized, a web interface is available for spells searching. An example of it without the pictures is available [there](https://azura-levidre.000webhostapp.com/spellsv2.php). Please be aware that MySQL could be down.
For updating the database with a new creature.jsonl or a new spell.jsonl, it's [here](https://azura-levidre.000webhostapp.com/bdd-envoi.php).

## How to use

The user selects the options in a form and send it. By default, all the options are set on Anything what means it won't select spells depending on it. The page then shows the spells depending on the form the user fulfilled.

Of course the user could want some particular spells, they can write some keywords in the search box so only the spells with those keywords in their description will be displayed.

The web interface will display the list of spells with for each one its card picture, its name and the creatures able to learn it if there are some. The spell name and the creatures ones are links to their pages.

## Technology

The form is in html and is sent with a post to the same page. The php code will retreive it and connects to the database, then it creates the SQL request and send it. It retrieves the response and verify it corresponds to the type selected in the form and the possible keywords. It will resend a new request to the database asking for the creatures learning each spell and display the spells.
The database itself is MySQL.
