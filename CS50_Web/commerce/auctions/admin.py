from django.contrib import admin
from .models import User, Listing, Bid, Comment
# Register your models here.

class ListingAdmin(admin.ModelAdmin):
    list_display = ("id", "user", "title", "description", "starting_bid", 'img_url', "category", 'closed')


admin.site.register(User)
# admin.site.register(Listing)
admin.site.register(Listing, ListingAdmin)
admin.site.register(Bid)
admin.site.register(Comment)
