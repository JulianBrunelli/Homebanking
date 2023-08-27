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
                title: 'Do you want to save the changes?',
                showDenyButton: true,
                confirmButtonText: 'Save',
                denyButtonText: `Don't save`,
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