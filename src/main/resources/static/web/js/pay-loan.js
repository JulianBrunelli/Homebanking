const { createApp } = Vue;

createApp({
    data() {
        return {
            loans: [],
            selectLoan: "",
            originAccounts: [],
            selectOriginAccount: "",
            amounts: [],
            amount: 0,
        };
    },
    created() {
        this.loadData()
    },
    methods: {
        loadData() {
            axios.get('/api/clients/current')
                .then(response => {
                    this.originAccounts = response.data.accounts
                    this.loans = response.data.loans
                    this.amounts = this.selectLoan.amount
                    console.log(this.selectOriginAccount);
                    console.log(this.loans);
                }).catch(error => console.error(error))
        },
        payLoan() {
            console.log(this.selectOriginAccount);
            console.log(this.amount);
            Swal.fire({
                title: 'Do you want to pay a fee?',
                showDenyButton: true,
                confirmButtonText: 'Carry out',
                denyButtonText: 'Cancel',
            })
                .then((result) => {
                    if (result.isConfirmed) {
                        axios.patch("/api/transactions/loans/current/pay", `idClientLoan=${this.selectLoan.id}&idAccount=${this.selectOriginAccount}&amount=${this.amount}`)
                            .then(response => {
                                Swal.fire('A fee has been paid!', '', 'success').then(response => {
                                    location.href = './accounts.html'
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
                        Swal.fire('The transaction was not carried out', '', 'info')
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
    computed: {
    }
}).mount("#app");