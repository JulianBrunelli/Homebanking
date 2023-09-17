const { createApp } = Vue;

createApp({
    data() {
        return {
            loans: [],
            nameLoan: "",
            maxAmount: null,
            payments: [],
            interest: null,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get("/api/loans")
                .then(response => {
                    this.loans = response.data
                })
                .catch((error) => error.response.data);
        },
        addLoan() {
            Swal.fire({
                text: 'Confirm loan request',
                showDenyButton: true,
                confirmButtonText: 'Confirm',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        let object = {
                            "name": this.nameLoan,
                            "maxAmount": this.maxAmount,
                            "payments": this.payments.sort((a, b) => a - b),
                            "interest": this.interest
                        }
                        axios.post("/api/loans/create", object)
                            .then(response => {
                                Swal.fire('Saved!', '', 'success')
                                    .then(response => {
                                        this.loans.push(response.data)
                                        this.nameLoan = ""
                                        this.maxAmount = null
                                        this.payments = []
                                        this.interest = null
                                        this.loadData()
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
                }).catch((error) => console.error(error))
        },
        signOut() {
            axios.post('/api/logout')
                .then(response => {
                    location.href = "../web/pages/index.html"
                })
                .catch((error) => console.error(error.message));
        },
    },
}).mount("#app");