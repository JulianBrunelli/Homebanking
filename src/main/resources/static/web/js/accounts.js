const { createApp } = Vue;

createApp({
    data() {
        return {
            clientsAccounts: [],
            json: null,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("http://localhost:8080/api/clients/1")
                .then(response => {
                    this.clientsAccounts = response.data.accounts
                    this.json = JSON.stringify(response.data, null, 1);
                    console.log(this.clientsAccounts);
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");