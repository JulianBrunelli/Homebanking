const { createApp } = Vue;

createApp({
    data() {
        return {
            cardsDebit: [],
            cardsCredit: [],
            cardsAll: [],
            fromDate: "",
            thruDate: "",
            json: null,
            loader: true,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("/api/clients/current")
                // { headers: { 'accept': 'application/xml' } }
                .then(response => {
                    this.cardsAll = response.data.cards
                    this.cardsDebit = response.data.cards.filter(card => card.type == 'DEBIT').filter(card => card.active)
                    this.cardsCredit = response.data.cards.filter(card => card.type == 'CREDIT').filter(card => card.active)
                    this.fromDate = response.data.cards.map(card => card.fromDate.slice(2, 7).replace(/-/g, '/'))
                    this.thruDate = response.data.cards.map(card => card.thruDate.slice(2, 7).replace(/-/g, '/'))
                    this.active = response.data.cards.filter(card => card.active)
                    this.json = JSON.stringify(response.data, null, 1);
                    this.loader = false
                })
                .catch((error) => console.error(error.message));
        },
        disabledCard(id) {
            Swal.fire({
                title: 'Are you sure you want to delete the card?',
                showDenyButton: true,
                confirmButtonText: 'Confirm',
                denyButtonText: 'Cancel',
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.patch("/api/clients/current/cards/deactivate", `id=${id}`)
                        .then(response => {
                            Swal.fire('Saved!', '', 'success')
                                .then(response => {
                                    location.href = '../pages/cards.html'
                                })
                        })
                        .catch((error) => {
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: error.response.data,
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