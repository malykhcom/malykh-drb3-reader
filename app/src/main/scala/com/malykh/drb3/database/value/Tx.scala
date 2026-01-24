package com.malykh.drb3.database.value

final case class Tx(txId: TxId, converter: Converter, dataId: DataId, bytes: Array[Byte],
                    nameId: StringId, serviceCategoryId: ServiceCategoryId)


