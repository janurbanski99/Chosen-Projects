from django.contrib.auth.models import AbstractUser
from django.db import models


class User(AbstractUser):
    watchlist = models.ManyToManyField('Listing', blank=True, null=True, related_name='watchlisted_by')


class Listing(models.Model):
    CATEGORY_CHOICES = {
        "FASHION": "Fashion",
        "TOYS": "Toys",
        "ELECTRONICS" : "Electronics",
        "HOME" : "Home",
        "OTHER" : "Other"
    }

    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="listings")
    # current_highest_bidder = models.ForeignKey(User, on_delete=models.CASCADE, related_name="winning_listings", blank=True, null=True)
    title = models.CharField(max_length=64)
    description = models.CharField(max_length=256)
    starting_bid = models.FloatField()
    current_prize = models.FloatField(blank=True, null=True)
    img_url = models.URLField(blank=True, null=True, max_length=400) # opcjonalne
    category = models.CharField(max_length=64,choices=CATEGORY_CHOICES, blank=True, null=True)
    closed = models.BooleanField()
    auction_winner = models.ForeignKey(User, on_delete=models.CASCADE, related_name="won_auctions", blank=True, null=True, default=None)

    def __str__(self):
        return f"{self.title} {self.description} {self.starting_bid} {self.img_url}"   #tu później do zmiany / przekminienia żeby pokazywało current prize
    
class Bid(models.Model):
    listing = models.ForeignKey(Listing, on_delete=models.CASCADE, related_name="bids")
    user =  models.ForeignKey(User, on_delete=models.CASCADE, related_name="user_bids")
    bid_amount = models.FloatField(blank=True, null=True)
    if_last_bid = models.BooleanField(default=True)

    def __str__(self):
        return f"Bid with id {self.id} concerning {self.listing} on the amount {self.bid_amount}"
    
class Comment(models.Model):
    listing = models.ForeignKey(Listing, on_delete=models.CASCADE, related_name="comments")
    written_by = models.ForeignKey(User, on_delete=models.CASCADE, related_name="user_comments")
    comment = models.CharField(max_length=256)

    def __str__(self):
        return f"Comment to listing {self.listing}: {self.comment}"