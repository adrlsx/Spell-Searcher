import scrapy
from pathlib import Path

from Items import ClassItem
from StringFunction import sanitize


class ClassSpider(scrapy.Spider):
    name = 'class'
    allowed_domains = ['aonprd.com']
    start_urls = ["https://aonprd.com/Spells.aspx"]

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
        # Get all hyperlink <a> containing 'Spells.aspx?Class='
        class_list = response.xpath("//a[contains(@href, 'Spells.aspx?Class=')]/text()").getall()
        # Delete the "All spells" entry as it is not a class
        del class_list[0]

        classes = ClassItem()
        # Sanitize each string in the array and sort it in alphabetical order
        classes["classes"] = sorted((sanitize(s) for s in class_list))
        # Return the class list as an object
        yield classes

