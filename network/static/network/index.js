document.addEventListener('DOMContentLoaded', function() {
    const body = document.querySelector('#post_content');

    document.querySelectorAll('.user_profile_link').forEach(element => {
        element.addEventListener('click', () => {
            const userId = element.dataset.userId;
            load_user_profile(userId);
        });
    })

    document.querySelectorAll('.edit-btn').forEach(element => {
        element.addEventListener('click', () => {
            const postId = element.dataset.postId;
            edit_post(postId, element);
        })
    })

    document.querySelectorAll('.like_unlike').forEach(element => {
        element.addEventListener('click', () => {
            const postId = element.dataset.postId;
            like_unlike(postId, element);
        })
    })

    const followBtn = document.querySelector('#follow_or_unfollow_button');
    if (followBtn) {
        followBtn.addEventListener('click', (event) => {
            follow_or_unfollow(event.target.dataset.userId);
        });
    }

    document.querySelector('button[type="submit"]').addEventListener('click', (event) => {
        event.preventDefault()
        fetch('/posts', {
            method: 'POST',
            body: JSON.stringify({
                body: body.value
            })
        })
        .then(response => {
            if (response.status == 201) {
                body.value = '';
                const alert = document.createElement('div');
                alert.className = 'alert alert-success mx-4';
                alert.innerText = 'New Post added!';
                alert.className = 'alert alert-success mx-4 mt-3';
                
                setTimeout(() => alert.remove(), 3000);
                console.log('Post created');
            }
            return response.json();
        })
        .then(result => {
            console.log(result);
        })
        .then(() => location.reload());

    })


})

// TODO najpierw - logika API do POSTA na url /posts

function load_user_profile(userId) {
    fetch(`/profile/${userId}`)
    .then (response => {
        if (response.status == 200) {
            return response.json();
        }
    })
    .then(result => console.log(result))
    // TODO - po napisaniu nowego posta - reload strony 
}


function follow_or_unfollow(userId) {
    fetch(`/follow_or_unfollow/${userId}`, {
        method: 'PUT'
    })
    .then(response => {
        if (!response.ok) {
            console.log('HTTP error', response.status);
            return;
        }
        return response.json();
    })
    .then(data => {
        if (data) console.log(data);
    })
    .then(() => location.reload());
}

function edit_post(postId, btn) {
    const card = btn.closest('.card');

    card.querySelector('.post-body').classList.add('d-none');
    card.querySelector('.post-edit-area').classList.remove('d-none');
    card.querySelector('.edit-btn').classList.add('d-none');
    card.querySelector('.save-btn').classList.remove('d-none');

    card.querySelector('.save-btn').addEventListener('click', () => {
        const newContent = card.querySelector('.post-edit-area').value;

        fetch(`/edit_post/${postId}`, {
            method: 'PUT',
            body: JSON.stringify({ body: newContent })
        })
        .then(response => response.json())
        .then(response => {
            card.querySelector('.post-body').innerText = response.body;
            card.querySelector('.post-body').classList.remove('d-none');
            card.querySelector('.post-edit-area').classList.add('d-none');
            card.querySelector('.save-btn').classList.add('d-none');
            card.querySelector('.edit-btn').classList.remove('d-none');
            return;
        });
    })
}

function like_unlike(postId, element) {
    fetch(`/like_unlike/${postId}`, {
        method: 'PUT'
    })
    .then(response => response.json())
    .then(data => {
        element.innerHTML = `❤️ ${data.likes}`;
    })
    return;
}