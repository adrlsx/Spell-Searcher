import scrapy.item


# More info: https://docs.scrapy.org/en/latest/topics/items.html
class CreatureItem(scrapy.Item):
    name = scrapy.Field()
    url = scrapy.Field()
    spells = scrapy.Field()
    description = scrapy.Field()


class ClassItem(scrapy.Item):
    classes = scrapy.Field()


class SchoolItem(scrapy.Item):
    schools = scrapy.Field()


class SpellItem(scrapy.Item):
    name = scrapy.Field()
    url = scrapy.Field()
    school = scrapy.Field()
    classes = scrapy.Field()
    components = scrapy.Field()
    spell_resistance = scrapy.Field()
    description = scrapy.Field()
