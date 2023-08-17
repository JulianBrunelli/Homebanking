const { createApp } = Vue;

createApp({
    data() {
        return {
            cardsDebit: [],
            cardsCredit: [],
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
            axios.get("http://localhost:8080/api/clients/1")
                .then(response => {
                    this.cardsDebit = response.data.cards.filter(card => card.type == 'DEBIT')
                    this.cardsCredit = response.data.cards.filter(card => card.type == 'CREDIT')
                    this.fromDate = response.data.cards.map(card => card.fromDate.slice(2, 7))
                    this.thruDate = response.data.cards.map(card => card.thruDate.slice(2, 7))
                    console.log(this.fromDate);
                    this.json = JSON.stringify(response.data, null, 1);
                    this.loader = false
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");