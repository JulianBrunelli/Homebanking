const { createApp } = Vue;

createApp({
    data() {
        return {
            accounts: [],
            accountBalance: "",
            transactions: [],
            loader: true,
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
                    this.accountBalance = this.accounts.balance.toLocaleString()
                    this.transactions = this.accounts.transactions.sort((a, b) => b.id - a.id)
                    this.date = this.transactions.map(transaction => transaction.date.slice(0, 10).replace(/-/g, '/'))
                    this.time = this.transactions.map(transiction => transiction.date.slice(14, -7))
                    this.loader = false
                })
                .catch((error) => location.href = "https://es.memedroid.com/memes/detail/2712377/Use-it-for-interesting-things");
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