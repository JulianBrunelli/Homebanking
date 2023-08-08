const { createApp } = Vue;

createApp({
    data() {
        return {
            idParams: null,
            accounts: [],
            transactions: [],
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("http://localhost:8080/api/accounts/1")
                .then(response => {
                    this.accounts = response.data
                    const parameter = location.search
                    const params = new URLSearchParams(parameter)
                    this.idParams = params.get("id")
                    this.transactions = this.accounts.transactions.sort((a, b) => b.id - a.id)
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");