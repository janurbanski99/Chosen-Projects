from django.contrib.auth import authenticate, login, logout
from django.db import IntegrityError
from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import render
from django.urls import reverse
from django.contrib.auth.decorators import login_required
from django.contrib import messages

from .models import User, Listing, Bid, Comment


def index(request):
    listings = Listing.objects.all()

    return render(request, "auctions/index.html", {
        "listings" : listings
    })


def login_view(request):
    if request.method == "POST":

        # Attempt to sign user in
        username = request.POST["username"]
        password = request.POST["password"]
        user = authenticate(request, username=username, password=password)

        # Check if authentication successful
        if user is not None:
            login(request, user)
            return HttpResponseRedirect(reverse("index"))
        else:
            return render(request, "auctions/login.html", {
                "message": "Invalid username and/or password."
            })
    else:
        return render(request, "auctions/login.html")


def logout_view(request):
    logout(request)
    return HttpResponseRedirect(reverse("index"))


def register(request):
    if request.method == "POST":
        username = request.POST["username"]
        email = request.POST["email"]

        # Ensure password matches confirmation
        password = request.POST["password"]
        confirmation = request.POST["confirmation"]
        if password != confirmation:
            return render(request, "auctions/register.html", {
                "message": "Passwords must match."
            })

        # Attempt to create new user
        try:
            user = User.objects.create_user(username, email, password)
            user.save()
        except IntegrityError:
            return render(request, "auctions/register.html", {
                "message": "Username already taken."
            })
        login(request, user)
        return HttpResponseRedirect(reverse("index"))
    else:
        return render(request, "auctions/register.html")
    
@login_required
def create_listing(request):
    if request.method == 'POST':
        title = request.POST['title']
        description = request.POST['description']
        starting_bid = request.POST['starting_bid']
        img_url = request.POST['img_url']
        category = request.POST.get('category', None)

        # if category is None:
        #     print("<><><><><><><> is NONE")
        user = request.user

        listing = Listing(title=title, description=description, starting_bid=starting_bid, img_url=img_url, category=category, user=user, closed=False)
        listing.save()
        messages.success(request, "Listing added successfully")
        return HttpResponseRedirect(reverse("index"))

    return render(request, "auctions/create_listing.html", {
        'categories' : Listing.CATEGORY_CHOICES
    })

@login_required
def go_to_listing(request, id):
    # all_listings = Listing.objects.all()
    all_ids = Listing.objects.values_list('id', flat=True)
    my_listing = Listing.objects.get(id=id)
    current_bidder = request.user
    watchlist_elements = request.user.watchlist.all()
    # bid_obj = Bid.objects.filter(listing=my_listing, user=current_bidder).first()

    if request.method == 'POST':
        form_type = request.POST.get("form_type")
        if form_type == "bid":
            bid_amount = request.POST['bid_amount']
            # if bid_obj is None:
            previous_bids = Bid.objects.all().filter(listing=my_listing)
            previous_bids.update(if_last_bid=False)     #wszystkie poprzednie już nie są true
            new_bid_obj = Bid(listing=my_listing, user=current_bidder, bid_amount=bid_amount)
            new_bid_obj.save()
            
            if my_listing.current_prize is None or float(bid_amount) > float(my_listing.current_prize):
                my_listing.current_prize = float(bid_amount)
                # my_listing.current_highest_bidder = current_bidder #tu musi być cała instancja User a nie tylko id
                my_listing.save()
                messages.success(request, f"Bid made succesfully")
                return HttpResponseRedirect(reverse("index"))
            else:
                messages.error(request, "Invalid bid amount")
                return HttpResponseRedirect(reverse("index"))
        elif form_type == 'watchlist' and my_listing not in watchlist_elements:
            request.user.watchlist.add(my_listing)
            messages.success(request, "Item added to the watchlist!")
            return HttpResponseRedirect(reverse("index"))
        elif form_type == 'watchlist' and my_listing in watchlist_elements:
            messages.error(request, "Item already in the watchlist!")
            return HttpResponseRedirect(reverse("index"))
        
    if id in list(all_ids):
        return render(request, "auctions/go_to_listing.html", {
            'my_listing' : my_listing,
            'listing_bids' : my_listing.bids.all(),
            'min_bid' : (my_listing.current_prize or my_listing.starting_bid) + 0.01,
            'comments' : my_listing.comments.all()
        })
    elif id not in list(all_ids):
        messages.error(request, "Listing with a given id does not exist")
        return HttpResponseRedirect(reverse("index"))
    
@login_required
def watchlist(request):
    return render(request, "auctions/watchlist.html", {
        'watchlist_elements' : request.user.watchlist.all()
    })

@login_required
def remove(request, id):
    if request.method == 'POST':
        request.user.watchlist.remove(request.user.watchlist.get(id=id))
        messages.success(request, "Listing removed from the watchlist")
        return HttpResponseRedirect(reverse("index"))
    
@login_required
def close(request, id):
    if request.method == 'POST':
        highest_bidder = None
        listing = Listing.objects.get(id=id)
        for bid in listing.bids.all():
            if bid.if_last_bid is True:
                highest_bidder = bid.user
                # print(highest_bidder)
        
        listing.closed = True
        listing.auction_winner = highest_bidder
        listing.save()
        messages.info(request, f"Auction closed. Won by {highest_bidder}")
        return HttpResponseRedirect(reverse("index"))
    

@login_required
def comment(request, id):
    if request.method == 'POST':
        content = request.POST['comment']
        my_listing = Listing.objects.get(id=id)
        new_comment = Comment(listing=my_listing, comment=content, written_by = request.user)
        new_comment.save()

        messages.success(request, "Comment added")
        return HttpResponseRedirect(reverse("go_to_listing", kwargs={'id': my_listing.id}))
    
def categories(request, chosen_cat=None):
    all_categories = Listing.objects.values_list('category', flat=True).distinct()

    if chosen_cat:
        listings = Listing.objects.filter(category=chosen_cat, closed=False)
    else:
        listings = None

    return render(request, 'auctions/categories.html', {
        # 'listings' : Listing.objects.all(),
        'categories' : all_categories,
        'listings' : listings,
        'chosen_cat' : chosen_cat
    })
