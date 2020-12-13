import scrapy
from pathlib import Path

from Items import CreatureItem
from StringFunction import sanitize


class CreatureSpider(scrapy.Spider):
    name = 'creature'
    allowed_domains = ['aonprd.com']
    start_urls = ["https://aonprd.com/Monsters.aspx?Letter=All"]

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
        # Get all hyperlink <a> inside a row <tr>
        bestiary = response.css("tr td a")

        for creature in bestiary:
            # Gets the name for every creature
            creature_name = creature.css("::text").get()
            # Remove the spaces at the left and at the right of each name and capitalize it
            creature_name = sanitize(creature_name)

            # Get the link of the current creature
            link = creature.css("::attr(href)").get()
            link = f"https://{self.allowed_domains[0]}/{link}"

            yield scrapy.Request(link, callback=self.parse_each_creature, meta={"name": creature_name, "url": link})

    @staticmethod
    def parse_each_creature(response):
        creature = CreatureItem()
        # Get the creature name and url
        creature["name"] = response.meta.get("name")
        creature["url"] = response.meta.get("url")

        # Get all spells associated to the current creature
        spell_list = response.css("span i::text").getall()
        # For each string verify if the initial is a lowercase letter
        # If it is a lower case letter it is a spell to add to the set (the set structure avoids duplicate)
        creature["spells"] = set(sanitize(spell) for spell in spell_list if spell[0].islower())

        # Get a short description of the creature
        # The first item in the spell list was discarded as its initial is not lowercase
        # But it actually corresponds to the short description
        creature["description"] = spell_list[0].strip()

        # If the list is empty mark it as False
        if not creature["spells"]:
            creature["spells"] = False

        yield creature
