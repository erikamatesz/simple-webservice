package br.com.radixeng.treinamentojava.app;

class Client implements IClient {
    
    private String name;

    Client(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return this.getName();
    }
}