const { createApp } = Vue;

createApp({
    data() {
        return {
            clients: [],
            firstName: "",
            lastName: "",
            email: "",
            json: null,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("http://localhost:8080/rest/clients")
                .then(response => {
                    this.clients = response.data._embedded.clients
                    this.json = JSON.stringify(response.data, null, 1);
                })
                .catch((error) => console.error(error.message));
        },
        checkInput() {
            if (this.firstName && this.lastName && this.email) {
                this.addClient()
            } else {
                alert("Please fill in the fields")
            }
        },
        addClient() {
            let newClient = { firstName: this.firstName, lastName: this.lastName, email: this.email }
            axios.post("http://localhost:8080/rest/clients", newClient)
                .then(response => {
                    this.clients.push(response.data)
                    this.firstName = "",
                        this.lastName = "",
                        this.email = "",
                        this.loadData();
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");