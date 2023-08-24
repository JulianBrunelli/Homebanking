const { createApp } = Vue;

createApp({
    data() {
        return {
            firstName: "",
            lastName: "",
            email: "",
            password: "",
        };
    },
    created() {
    },
    methods: {
        checkLogIn(event) {
            event.preventDefault();
            if (this.email && this.password) {
                this.logIn()
            } else {
                alert("Please fill in the fields")
            }
        },
        logIn() {
            axios.post('/api/login', `email=${this.email}&password=${this.password}`,
                { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    if (this.email == "julianbrunelli@outlook.com") {
                        location.href = "../../admin/manager.html"
                    } else {
                        location.href = "./accounts.html"
                    }
                })
                .catch((error) => alert("The password or username is incorrect"));
        },
        signOut() {
            axios.post('/api/logout')
                .then(response => {
                    location.href = "../pages/index.html"
                    this.form = true
                })
                .catch((error) => console.error(error.message));
        },
        checkSingUp(event) {
            event.preventDefault();
            if (this.firstName && this.lastName && this.email && this.password) {
                this.addClient()
            } else {
                alert("Please fill in the fields")
            }
        },
        addClient() {
            axios.post('/api/clients', `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,
                { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    this.logIn()
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");