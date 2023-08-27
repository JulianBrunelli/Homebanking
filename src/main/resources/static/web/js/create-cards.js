const { createApp } = Vue;

createApp({
    data() {
        return {
            cardsType: [],
        };
    },
    methods: {
        addCard() {
            Swal.fire({
                title: 'Confirm card request',
                showDenyButton: true,
                confirmButtonText: 'Confirm',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        axios.post("/api/clients/current/cards")
                            .then(response => {
                                Swal.fire('Saved!', '', 'success').then(response => {
                                    location.href = '../pages/cards.html'
                                })
                            })
                    } else {
                        Swal.fire('Changes are not saved', '', 'info')
                    }
                })
        },
        signOut() {
            axios.post('/api/logout')
                .then(response => {
                    location.href = "../pages/index.html"
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");