const { createApp } = Vue;

createApp({
    data() {
        return {
            cardsType: [],
            cardsColor: [],
        };
    },
    created() {
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
                        axios.post("/api/clients/current/cards", `type=${this.cardsType}&color=${this.cardsColor}`)
                            .then(response => {
                                Swal.fire('Saved!', '', 'success')
                                    .then(response => {
                                        location.href = '../pages/cards.html'
                                    }).catch(error => {
                                        Swal.fire({
                                            icon: 'error',
                                            title: 'Oops...',
                                            text: error.response.data,
                                        })
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