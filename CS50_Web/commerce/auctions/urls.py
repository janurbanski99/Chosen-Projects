from django.urls import path

from . import views

urlpatterns = [
    path("", views.index, name="index"),
    path("login", views.login_view, name="login"),
    path("logout", views.logout_view, name="logout"),
    path("register", views.register, name="register"),
    path("create_listing", views.create_listing, name="create_listing"),
    path("go_to_listing/<int:id>", views.go_to_listing, name="go_to_listing"),
    path('watchlist', views.watchlist, name='watchlist'),
    path('remove/<int:id>', views.remove, name='remove'),
    path('close/<int:id>', views.close, name='close'),
    path('comment/<int:id>', views.comment, name='comment'),
    path('categories', views.categories, name='categories'),
    path('categories/<str:chosen_cat>', views.categories, name='chosen_cat')
]
