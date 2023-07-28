variable "cluster_name" {
    description = "The name to use for all the cluster resources"
    type = string
}

## ---- Nodes layer definition ----
variable "layer" {
    description = "The name of an isolated layer hosting modules"
    type = string
}
