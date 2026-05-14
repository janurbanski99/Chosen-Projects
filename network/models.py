from django.contrib.auth.models import AbstractUser
from django.db import models
from django.core.exceptions import ValidationError


class User(AbstractUser):
    following = models.ManyToManyField(
        "self",
        symmetrical=False, #nie jest wzajemne automatycznie
        blank=True,
        related_name="followers"
    )
    
    def follow(self, user):
        if user == self:
            raise ValidationError ("You can't follow yourself")
        self.following.add(user)

    def unfollow(self, user):
        if user == self:
            raise ValidationError ("You can't follow yourself")
        self.following.remove(user) 

    def serialize(self):
        return {
            "id" : self.id,
            'username': self.username,
            'following': [{'id': user.id, 'username': user.username} for user in self.following.all()],
            'followers': [{'id': user.id, 'username': user.username} for user in self.followers.all()]
        }

class Post(models.Model):
    user = models.ForeignKey("User", on_delete=models.CASCADE, related_name="user_posts")
    body = models.TextField(blank=True)
    timestamp = models.DateTimeField(auto_now_add=True)
    likes = models.IntegerField(default=0)
    liked_by = models.ManyToManyField("User", blank=True, related_name="liked_posts")

    def serialize(self):
        return {
            "id": self.id,
            "user": self.user.username,
            "body": self.body,
            "timestamp": self.timestamp.strftime("%b %d %Y, %I:%M %p"),
            "likes": self.likes
        }
    
    def like(self, user):
        self.liked_by.add(user)
        self.likes = self.liked_by.count()
        self.save()
    
    def unlike(self, user):
        self.liked_by.remove(user)
        self.likes = self.liked_by.count()
        self.save()