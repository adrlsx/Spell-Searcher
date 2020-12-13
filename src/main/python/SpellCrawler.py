import scrapy
from pathlib import Path

from Items import SpellItem
from StringFunction import sanitize, parse_school, parse_classes, parse_components, parse_spell_resistance, \
    parse_description


class SpellSpider(scrapy.Spider):
    name = 'spell'
    allowed_domains = ['aonprd.com']
    start_urls = ["https://aonprd.com/Spells.aspx?Class=All"]

    # More info: https://docs.scrapy.org/en/latest/topics/broad-crawls.html
    # https://stackoverflow.com/questions/17029752/speed-up-web-scraper
    custom_settings = {
        'BOT_NAME': f'{name}_crawler',
        'FEEDS': {
            # Path for the JSON output of the crawler
            Path(f'output/{name}.jsonl').resolve(): {
                'format': 'jsonlines',
                'encoding': 'utf-8',
                'overwrite': True,
            },
        },
        # Log level for the current spider, available levels are: CRITICAL, ERROR, WARNING, INFO, DEBUG
        'LOG_LEVEL': 'WARNING',
        # Path for the logs
        'LOG_FILE': Path(f'logs/{name}.log').resolve(),
        'DEPTH_LIMIT': 1,
    }

    def parse(self, response, **kwargs):
        # Get all hyperlink <a> inside bold text <b> inside a cell <tr>
        spells = response.css("tr b a")

        for spell in spells:
            # Gets the name for every spell
            spell_name = spell.css("::text").getall()
            # In some cases the first entry might be a space, so always select the first entry from the end
            # This is the case when there is an icon: https://aonprd.com/SpellDisplay.aspx?ItemName=Admonishing%20Ray
            spell_name = sanitize(spell_name[-1])
            # Get the link of the current spell
            link = spell.css("::attr(href)").get()
            link = f"https://{self.allowed_domains[0]}/{link}"

            yield scrapy.Request(link, callback=self.parse_each_spell, meta={"name": spell_name, "url": link})

    @staticmethod
    def parse_each_spell(response):
        spell = SpellItem()
        # Get the spell name and url
        spell["name"] = response.meta.get("name")
        spell["url"] = response.meta.get("url")

        # Multiple spells can be on the same page, spell_number allows us to distinguish them
        spell_number = 0
        current_name = str()
        # Increment the spell number until the name of the current spell corresponds to the one we are looking for
        while current_name != spell["name"]:
            spell_number += 1
            current_name = response.css(f"h1.title:nth-of-type({spell_number})::text").getall()
            # In some cases the first entry might be a space, so always select the first entry from the end
            current_name = sanitize(current_name[-1])

        # Parse and save each component of the spell
        spell["school"] = parse_school(response, spell_number)
        spell["classes"] = parse_classes(response, spell_number)
        spell["components"] = parse_components(response, spell_number)
        spell["spell_resistance"] = parse_spell_resistance(response, spell_number)
        spell["description"] = parse_description(response, spell_number, spell["name"])

        yield spell
