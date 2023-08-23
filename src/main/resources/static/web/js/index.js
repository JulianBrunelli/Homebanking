const { createApp } = Vue;

createApp({
    data() {
        return {
            json: null,
            loader: true,
        };
    },
    created() {
        // this.loadData()
    },
    // methods: {
    //     loadData() {
    //         axios.get("http://localhost:8080/api/clients/current")
    //             .then(response => {
    //                 this.json = JSON.stringify(response.data, null, 1);
    //                 this.loader = false
    //             })
    //             .catch((error) => console.error(error.message));
    //     },
    // },
}).mount("#app");