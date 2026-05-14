import json
from django.contrib.auth import authenticate, login, logout
from django.db import IntegrityError
from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import render
from django.urls import reverse
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.decorators import login_required
from django.shortcuts import render, get_object_or_404

from django.core.paginator import Paginator

from .models import User, Post

POST_BY_PAGE = 10

def index(request):
    paginator = Paginator( Post.objects.all().order_by('-timestamp'), POST_BY_PAGE)
    page_number = request.GET.get('page')
    post_obj = paginator.get_page(page_number)

    context = {
        'posts' : Post.objects.all().order_by('-timestamp'),
        'post_obj' : post_obj
    }
    return render(request, "network/index.html", context)

@csrf_exempt
@login_required
def makepost(request):
    if request.method != 'POST':
        return JsonResponse({"error": "POST request required."}, status=400)

    data = json.loads(request.body)
    post_content = data.get("body")

    post = Post(
        user = request.user,
        body = post_content
    )
    post.save()
    return JsonResponse({"message" : "Post added"}, status=201)

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
            return render(request, "network/login.html", {
                "message": "Invalid username and/or password."
            })
    else:
        return render(request, "network/login.html")


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
            return render(request, "network/register.html", {
                "message": "Passwords must match."
            })

        # Attempt to create new user
        try:
            user = User.objects.create_user(username, email, password)
            user.save()
        except IntegrityError:
            return render(request, "network/register.html", {
                "message": "Username already taken."
            })
        login(request, user)
        return HttpResponseRedirect(reverse("index"))
    else:
        return render(request, "network/register.html")

def load_profile(request, user_id):
    try:
        user = User.objects.get(pk=user_id)
    except User.DoesNotExist:
        return JsonResponse({'error': 'User not found'}, status=404)
    
    if request.method == 'GET':
        return JsonResponse(user.serialize())
    else:
        return JsonResponse({
            "error": "GET request required."
        }, status=400)
    
def load_user_profile_page(request):
    user_id = request.GET.get('id')
    user = get_object_or_404(User, pk=user_id)
    posts = user.user_posts.all().order_by('-timestamp')
    is_following = False
    is_following = user in request.user.following.all()

    paginator = Paginator(posts, POST_BY_PAGE)
    page_number = request.GET.get('page')
    post_obj = paginator.get_page(page_number)

    return render(request, 'load_user_profile_page.html', {
        'profile_user': user,
        'posts': posts,
        'num_followed_by': user.followers.count(),
        'num_following': user.following.count(),
        'is_following': is_following,
        'post_obj' : post_obj
    })

@csrf_exempt
def follow_or_unfollow(request, id):
    if request.method == 'PUT':
        current_user = request.user
        button_user_obj = get_object_or_404(User, pk=id)

        current_user_follows = current_user.following.values_list('id', flat=True)
        if id not in current_user_follows:
            current_user.follow(button_user_obj)
        else:
            current_user.unfollow(button_user_obj)
        return JsonResponse({'status' : 'ok'}, status = 200)
    else:
        return JsonResponse({
            'error': 'PUT request required'
        }, status=400)

@login_required
def following(request):
    user = request.user
    followed_users = user.following.all()
    followed_users_posts = Post.objects.filter(user__in=followed_users).order_by('-timestamp')

    paginator = Paginator(followed_users_posts, POST_BY_PAGE)
    page_number = request.GET.get('page')
    post_obj = paginator.get_page(page_number)

    return render(request, 'following.html', {
        'profile_user': user,
        'followed_users_posts': followed_users_posts,
        'followed_users' : followed_users,
        'post_obj' : post_obj
    })

@csrf_exempt
def edit_post(request, post_id):
    post = get_object_or_404(Post, pk=post_id)
    if post.user != request.user:
        return JsonResponse({'error': 'Unauthorized'}, status=403)
    
    if request.method == 'PUT':
        data = json.loads(request.body)
        post.body = data['body']
        post.save()
        return JsonResponse({'success': True, 'body': post.body})

@csrf_exempt
def like_unlike(request, post_id):
    post = get_object_or_404(Post, pk=post_id)
    if request.method == 'PUT':
        if request.user in post.liked_by.all():
            post.unlike(request.user)
        else:
            post.like(request.user)
        return JsonResponse({'likes': post.likes})