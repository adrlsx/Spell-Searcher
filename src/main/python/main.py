import time
from pathlib import Path
from scrapy.crawler import CrawlerProcess

from SpellCrawler import SpellSpider
from SchoolCrawler import SchoolSpider
from ClassCrawler import ClassSpider
from CreatureCrawler import CreatureSpider


def main():
    start = time.time()

    # If the crawler is started then the old output and logs become obsolete and need to be removed

    log_directory = Path("logs").resolve()
    # Creates the log directory if it does not already exist
    if not log_directory.is_dir():
        log_directory.mkdir()
    else:
        # Removes every log in the log directory
        for file in Path.iterdir(log_directory):
            file.unlink()

    output_directory = Path("output").resolve()
    # Creates the output directory if it does not already exist
    if not output_directory.is_dir():
        output_directory.mkdir()
    else:
        # Removes every result in the output directory
        for file in Path.iterdir(output_directory):
            file.unlink()

    print("Scrapy initialization...")
    # More info: https://doc.scrapy.org/en/latest/topics/settings.html
    # https://docs.scrapy.org/en/latest/topics/practices.html
    # https://docs.scrapy.org/en/latest/topics/exporters.html
    process = CrawlerProcess(settings={
        'COOKIES_ENABLED': False,
        'CONCURRENT_ITEMS': 100,
        'CONCURRENT_REQUESTS': 128,
        'CONCURRENT_REQUESTS_PER_DOMAIN': 128,
        'HTTPCACHE_ENABLED': True,
        # Log level for the global process, available levels are: CRITICAL, ERROR, WARNING, INFO, DEBUG
        'LOG_LEVEL': 'WARNING',
        # Common User Agent, more info: https://coderslegacy.com/python/scrapy-user-agents/
        'USER_AGENT': 'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)',
    })
    # Crawls the bestiary and output the list of every creature and their corresponding spell
    process.crawl(CreatureSpider)
    # Output the list of every class
    process.crawl(ClassSpider)
    # Output the list of every school
    process.crawl(SchoolSpider)
    # Output the list of every spell and their corresponding components
    process.crawl(SpellSpider)
    # The script will block here until the crawling is finished
    process.start()

    end = time.time()
    print("Execution time:", end - start, "s")


if __name__ == '__main__':
    main()
