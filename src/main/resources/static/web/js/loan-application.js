const { createApp } = Vue;

createApp({
    data() {
        return {
            loans: [],
            selectLoan: "",
            originAccounts: [],
            selectOriginAccount: "",
            selectPayment: "",
            amount: 0,
            finalAmount: null,
        };
    },
    created() {
        this.loadLoans()
        this.loadData()
    },
    methods: {
        loadLoans() {
            axios.get('/api/loans')
                .then(response => {
                    this.loans = response.data
                }).catch(error => console.log(error))
        },
        loadData() {
            axios.get('/api/clients/current')
                .then(response => {
                    this.originAccounts = response.data.accounts
                }).catch(error => console.log(error))
        },
        porcent() {
            this.finalAmount = this.amount + (this.amount * 0.2)
        },
        addLoan() {
            Swal.fire({
                title: 'Confirm loan request',
                showDenyButton: true,
                confirmButtonText: 'Confirm',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        let object = {
                            "id": this.selectLoan.id,
                            "amount": this.amount,
                            "payments": this.selectPayment,
                            "numberAccountDestination": this.selectOriginAccount
                        }
                        console.log(this.originAccounts);
                        axios.post("/api/loans", object)
                            .then(response => {
                                Swal.fire('Saved!', '', 'success')
                                    .then(response => {
                                        location.href = '../pages/accounts.html'
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