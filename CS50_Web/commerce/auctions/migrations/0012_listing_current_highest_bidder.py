# Generated by Django 5.1.6 on 2025-03-05 15:54

import django.db.models.deletion
from django.conf import settings
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('auctions', '0011_alter_listing_current_prize'),
    ]

    operations = [
        migrations.AddField(
            model_name='listing',
            name='current_highest_bidder',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='winning_listings', to=settings.AUTH_USER_MODEL),
        ),
    ]
