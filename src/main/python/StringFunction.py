import scrapy
import string
from typing import Union, List, Dict, Set


# Sanitize scraped data to have an homogeneous output
def sanitize(scraped_string: str) -> str:
    return scraped_string.strip().lower().capitalize()


def parse_school(response: scrapy.http.Response, spell_number: int) -> Union[str, bool]:
    # Get the text corresponding to the school of the spell
    school = response.xpath(f"//b[text() = 'School'][{spell_number}]/following-sibling::u/a/text()").get()
    if school:
        return sanitize(school)
    else:
        return False


def parse_classes(response: scrapy.http.Response, spell_number: int) -> Union[Dict[str, int], bool]:
    class_list = response.xpath(
        # Get the text following a bold tag <b> containing "Level"
        f"//b[text() = 'Level'][{spell_number}]/following-sibling::text()").get()
    if class_list and ';' not in class_list:
        if class_list[-1] == ')':
            # Removes the useless additional information for wizard
            # Example: Elemental Mastery - https://aonprd.com/SpellDisplay.aspx?ItemName=Elemental%20Mastery
            class_list = class_list.rsplit('(', 1)[0]
        # Split each class and its corresponding level in an array
        class_list = class_list.split(',')

        # List of dictionary object for each class
        name_level_list = list()
        # For each class in the array saves the name as the key and the level as the value of the dictionary
        for each_class in class_list:
            name = sanitize(each_class)
            # The level is an integer value between 0 and 9 at the end of the string
            level = int(name[-1])
            # The name is the rest of the string, without the level and the space at the end
            name = name[:-2]
            # Initialize an empty dictionary to store each class information
            classes = dict()
            classes["name"] = name
            classes["level"] = level
            name_level_list.append(classes)
        return name_level_list
    else:
        return False


def parse_components(response: scrapy.http.Response, spell_number: int) -> Union[List[str], bool]:
    components = response.xpath(
        # Get the text following a bold tag <b> containing "Components"
        f"//b[text() = 'Components'][{spell_number}]/following-sibling::text()").get()
    if components:
        # Creates a list with every component separated by a space
        components = components.split()
        # Valid components are exclusively uppercase, removes the components that aren't
        components = (c for c in components if c.isupper())  # Generator expression
        # Removes punctuation from the beginning and the end of every component
        components = [c.strip(string.punctuation) for c in components]  # List comprehension
        return components
    else:
        return False


def parse_spell_resistance(response: scrapy.http.Response, spell_number: int) -> bool:
    spell_resistance = response.xpath(
        # Get the text following a bold tag <b> containing "Spell Resistance"
        f"//b[text() = 'Spell Resistance'][{spell_number}]/following-sibling::text()").get()
    if spell_resistance:
        spell_resistance = sanitize(spell_resistance)
        # Return True if Spell Resistance is marked as "Yes" or False elsewhere
        return spell_resistance == "Yes"
    else:
        return False


def parse_description(response: scrapy.http.Response, spell_number: int, spell_name: str) -> Union[Set[str], bool]:
    description = response.xpath(
        # Get all elements following a <h3> heading with the text "Description" and before the next <h1> heading
        # More info: https://stackoverflow.com/questions/10176657/xpath-count-function
        # Text inside <i> tag is not taken into account as it refers to spell name
        f"//h3[text() = 'Description'][{spell_number}]"
        f"/following-sibling::text()[count(preceding::h1) = {spell_number+1}]").getall()
    if description:
        # Add the name of the spell in the description to improve the quality of the full text search
        description.append(spell_name)
        # Merge every part of the description into a single string
        description = ' '.join(description)
        # Convert alphanumeric characters in lowercase and replace other characters by spaces
        description = ''.join(d.lower() if d.isalnum() else ' ' for d in description)
        # Split the string into a set to avoid duplicate words
        # The default separator is any whitespace
        description = set(description.split())
        return description
    else:
        return False
