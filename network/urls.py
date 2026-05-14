
from django.urls import path
from django.contrib import admin

from . import views

urlpatterns = [
    path("", views.index, name="index"),
    path("login", views.login_view, name="login"),
    path("logout", views.logout_view, name="logout"),
    path("register", views.register, name="register"),
    path("admin/", admin.site.urls),

    path("posts", views.makepost, name="makepost"),
    path("profile/<int:user_id>", views.load_profile, name="load_profile"),
    path("users/", views.load_user_profile_page, name="load_user_profile_page"),
    path('follow_or_unfollow/<int:id>', views.follow_or_unfollow, name='follow_or_unfollow'),
    path('following', views.following, name='following'),
    path('edit_post/<int:post_id>', views.edit_post, name='edit_post'),
    path('like_unlike/<int:post_id>', views.like_unlike, name="like_unlike")
]
