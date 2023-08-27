const { createApp } = Vue;

createApp({
    data() {
        return {
            accounts: [],
            transactions: [],
            // loader: true,
            date: "",
            time: "",
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            const parameter = location.search
            const params = new URLSearchParams(parameter)
            const idParams = params.get("id")
            axios.get(`/api/clients/accounts/${idParams}`)
                .then(response => {
                    this.accounts = response.data
                    this.transactions = this.accounts.transactions.sort((a, b) => b.id - a.id)
                    this.date = this.transactions.map(transaction => transaction.date.slice(0, 10).replace(/-/g, '/'))
                    this.time = this.transactions.map(transiction => transiction.date.slice(14, -7))
                    // this.loader = false
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