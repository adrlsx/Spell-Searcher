import scrapy
from pathlib import Path

from Items import SchoolItem
from StringFunction import sanitize


class SchoolSpider(scrapy.Spider):
    name = 'school'
    allowed_domains = ['aonprd.com']
    start_urls = ["https://aonprd.com/SpellDefinitions.aspx"]

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
        # Get all <h2> heading with the css class title
        school_list = response.css("h2.title::text").getall()

        # Initialize the empty list of valid school
        schools = SchoolItem()
        schools["schools"] = list()

        # Schools are listed in alphabetical order
        # If a school in the list has an initial smaller than the previous one it means it is a subschool
        previous_initial = 'A'
        for school in school_list:
            # Check if all schools have been listed
            if school[0] < previous_initial:
                break
            else:
                # Add the valid school to the object
                schools["schools"].append(sanitize(school))
            # Save the current school initial to compare it with the next one
            previous_initial = school[0]

        # Return the school list as an object
        yield schools
