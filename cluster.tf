provider "azurerm" {
  features {}
  subscription_id = "e34a73df-5393-4133-9e24-a5a161591db4"
}

resource "azurerm_resource_group" "myterraformgroup" {
  name     = "myResourceGroup"
  location = "Australia Central"
}

resource "azurerm_kubernetes_cluster" "myakscluster" {
  name                = "myAKSCluster"
  location            = azurerm_resource_group.myterraformgroup.location
  resource_group_name = azurerm_resource_group.myterraformgroup.name
  dns_prefix          = "myaksdns"

  default_node_pool {
    name            = "default"
    node_count      = 2
    vm_size         = "Standard_DS2_v2"
    os_disk_size_gb = 30
  }

  identity {
    type = "SystemAssigned"
  }

  tags = {
    environment = "Terraform Demo"
  }
}