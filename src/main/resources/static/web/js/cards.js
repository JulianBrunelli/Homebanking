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
            axios.get("http://localhost:8080/api/clients/current")
                .then(response => {
                    this.cardsAll = response.data.cards
                    this.cardsDebit = response.data.cards.filter(card => card.type == 'DEBIT')
                    this.cardsCredit = response.data.cards.filter(card => card.type == 'CREDIT')
                    this.fromDate = response.data.cards.map(card => card.fromDate.slice(2, 7).replace(/-/g, '/'))
                    this.thruDate = response.data.cards.map(card => card.thruDate.slice(2, 7).replace(/-/g, '/'))
                    this.json = JSON.stringify(response.data, null, 1);
                    this.loader = false
                })
                .catch((error) => console.error(error.message));
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