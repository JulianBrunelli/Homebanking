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
        logIn() {
            axios.post('/api/login', `email=${this.email}&password=${this.password}`)
                .then(response => {
                    if (this.email == "julianbrunelli@outlook.com") {
                        location.href = "../../admin/pages/manager.html"
                    } else {
                        location.href = "./accounts.html"
                    }
                })
                .catch((error) => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: error.response.data,
                    })
                })
        },
        addClient() {
            Swal.fire({
                title: 'Do you want to create a user?',
                showDenyButton: true,
                confirmButtonText: 'Confirm',
                denyButtonText: 'Cancel',
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/clients', `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`)
                        .then(response => {
                            Swal.fire(type = 'success', '', 'success')
                                .then(response => {
                                    this.logIn()
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