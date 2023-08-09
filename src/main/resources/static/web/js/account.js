const { createApp } = Vue;

createApp({
    data() {
        return {
            accounts: [],
            transactions: [],
            loader: true,
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
            axios.get(`http://localhost:8080/api/accounts/${idParams}`)
                .then(response => {
                    this.accounts = response.data
                    this.transactions = this.accounts.transactions.sort((a, b) => b.date - a.date)
                    console.log(this.transactions);
                    this.loader = false
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");